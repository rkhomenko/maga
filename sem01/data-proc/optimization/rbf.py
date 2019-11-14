import abc
import numpy as np
from scipy.optimize import minimize as sc_minimize
import matplotlib.pyplot as plt
import torch


class RBFNetBase(metaclass=abc.ABCMeta):
    def __init__(self, n_components):
        self.n_components = n_components
        self.theta = None
        self.x_dim = 0

    def split_theta(self, theta):
        w = theta[:self.n_components]
        e = theta[self.n_components:2 * self.n_components]
        b = theta[2 * self.n_components:2 * self.n_components + self.n_components * self.x_dim] \
            .reshape((self.n_components, self.x_dim))

        return w, e, b

    def _h_helper(self, x, w, e, b):
        return w * np.exp(-e * (x - b.T) ** 2)

    def h_split(self, theta, X):
        w, e, b = self.split_theta(theta)
        return np.apply_along_axis(lambda x: self._h_helper(x, w, e, b), arr=X, axis=1)

    def h(self, theta, X):
        h_row_wise = np.sum(self.h_split(theta, X), axis=-1)
        return h_row_wise.reshape((X.shape[0], X.shape[1]))

    def fit(self, X):
        return self.h(self.get_theta(), X)

    @abc.abstractmethod
    def train(self, X, y, theta0=None, epochs=1000, eps=1e-6):
        raise NotImplementedError

    def set_x_dim(self, dim):
        self.x_dim = dim

    def get_x_dim(self):
        return self.x_dim

    def get_theta(self):
        return self.theta

    def set_theta(self, theta):
        self.theta = theta

    def get_params(self):
        return self.split_theta(self.get_theta())

    def get_n_components(self):
        return self.n_components


class RBFNetTorch(RBFNetBase):
    def train(self, X, y, theta0=None, epochs=1000, eps=1e-6):
        self.set_x_dim(X.shape[1])

        device = 'cuda' if torch.cuda.is_available() else 'cpu'

        X = torch.from_numpy(X).float().to(device)
        y = torch.from_numpy(y).float().to(device)

        w = torch.rand(self.get_n_components(), dtype=torch.float, requires_grad=True, device=device)
        e = torch.rand(self.get_n_components(), dtype=torch.float, requires_grad=True, device=device)
        b = torch.rand(size=(self.get_n_components(), self.get_x_dim()),
                       dtype=torch.float, requires_grad=True, device=device)

        alpha = 0.01

        print(X.shape)
        print(w.shape)
        print(e.shape)
        print(b.shape)

        optimizer = torch.optim.SGD([w, e, b], lr=alpha, momentum=0.9)
        loss_fn = torch.nn.MSELoss(reduction='mean')

        loss_arr = []

        epoch = 0
        while True:
            optimizer.zero_grad()
            yhat = (w * torch.exp(-e * torch.norm(X - b, dim=1) ** 2)).reshape(y.shape)

            loss = loss_fn(y, yhat)
            loss.backward()
            optimizer.step()

            loss_arr.append(loss.item())
            epoch += 1

            if epoch > epochs or loss.item() < eps:
                break

        w = w.cpu().detach().numpy()
        e = e.cpu().detach().numpy()
        b = b.flatten().cpu().detach().numpy()

        self.set_theta(np.hstack((w, e, b)))

        return loss_arr


class RBFNetScipy(RBFNetBase):
    def grad(self, theta, X, y):
        w, e, b = self.split_theta(theta)

        dw = 2 * np.sum((self.h(theta, X) - y) * np.exp(-e * (X - b.T) ** 2), axis=0) / X.shape[0]
        de = -2 * np.sum((self.h(theta, X) - y) * w * np.exp(-e * (X - b.T) ** 2) * (X - b.T) ** 2, axis=0) / X.shape[0]
        db = 4 * np.sum((self.h(theta, X) - y) * w * np.exp(-e * (X - b.T) ** 2) * e.T * (X - b.T), axis=0) / X.shape[0]
        return np.hstack((dw, de, db))

    def train(self, X, y, theta0=None, epochs=1000, eps=1e-6):
        self.set_x_dim(X.shape[1])

        theta = np.random.rand(2 * self.get_n_components() + self.get_n_components() * self.get_x_dim()) * 5 \
            if not theta0 else theta0

        print('theta0:', theta)
        # tol = 1
        # iter = 0
        # while iter < epochs and tol > eps:
        #     grad = self.grad(theta, X, y)
        #     theta -= 0.01 * grad
        #
        #     if iter % 10 == 0:
        #         print('Theta:', theta, '|grad|: ', np.linalg.norm(grad))
        #
        #     iter += 1

        x = sc_minimize(lambda theta: np.sum((self.h(theta, X) - y) ** 2 / X.shape[0]),
                        theta,
                        jac=lambda theta: self.grad(theta, X, y),
                        method='CG')
        print(x)
        print(np.sum((self.h(x.x, X) - y) ** 2))

        plt.plot(X, y)
        plt.plot(X, self.h(x.x, X))
        plt.show()

X = np.linspace(0, 10, 100).reshape((100, 1))
y = 10 * np.exp(- 1.5 * (X - 5) ** 2) + 5 * np.exp(-0.5 * (X - 8) ** 2) + 8 * np.sin(X) + 10

net = RBFNetScipy(n_components=5)
net.train(X, y, epochs=20000)
