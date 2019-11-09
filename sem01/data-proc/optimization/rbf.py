import abc
import numpy as np
from scipy.optimize import minimize as sc_minimize
import torch


class RBFNetBase(metaclass=abc.ABCMeta):
    def __init__(self, n_components):
        self.n_components = n_components
        self.theta = None
        self.x_dim = 0

    def _split_theta(self):
        w = self.theta[:self.n_components]
        e = self.theta[self.n_components:2 * self.n_components]
        b = self.theta[2 * self.n_components:2 * self.n_components + self.n_components * self.x_dim] \
            .reshape((self.n_components, self.x_dim))

        return w, e, b

    def h_split(self, X):
        w, e, b = self._split_theta()
        return np.apply_along_axis(lambda x: w * np.exp(-e * np.linalg.norm(x.T - b, axis=-1) ** 2),
                                   arr=X, axis=1)

    def h(self, X):
        h_row_wise = np.sum(self.h_split(X), axis=1)
        return h_row_wise.reshape((X.shape[0], X.shape[1]))

    def fit(self, X):
        return self.h(X)

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
        return self._split_theta()

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


class RBFNet:
    def __init__(self, rbf_num):
        self.rbf_num = rbf_num
        self.minimize = lambda f, x0, jac=None: sc_minimize(
            f, x0, method='Nelder-Mead', options={'maxiter': 20000})
        self.w = None
        self.e = None
        self.b = None
        self.theta = None

    def __set_w(self, w):
        self.w = w

    def __set_e(self, e):
        self.e = e

    def __set_b(self, b):
        self.b = b

    def set_theta(self, theta):
        self.theta = theta

    def get_rbf_num(self):
        return self.rbf_num

    def get_minimize(self):
        return self.minimize

    def get_theta(self):
        return self.theta

    def get_params(self):
        return self.w, self.e, self.b

    @staticmethod
    def __split_theta(theta, rbf_num, m):
        w = theta[:rbf_num]
        e = theta[rbf_num:2 * rbf_num]
        b = theta[2 * rbf_num:2 * rbf_num + rbf_num * m].reshape((rbf_num, m))

        return w, e, b

    @staticmethod
    def __h_helper(x, w, e, b):
        return w * np.exp(-e * np.linalg.norm(x.T - b, axis=-1) ** 2)

    @staticmethod
    def h_split(X, theta, rbf_num):
        w, e, b = RBFNet.__split_theta(theta, rbf_num, X.shape[1])
        return np.apply_along_axis(lambda x: RBFNet.__h_helper(x, w, e, b), arr=X, axis=1)

    @staticmethod
    def h(X, theta, rbf_num):
        h_row_wise = np.sum(RBFNet.h_split(X, theta, rbf_num), axis=1)
        return h_row_wise.reshape((X.shape[0], X.shape[1]))

    @staticmethod
    def J(X, y, theta, rbf_num):
        return np.sum((RBFNet.h(X, theta, rbf_num) - y) ** 2) / 2 / X.shape[0]

    @staticmethod
    def J_der(X, y, theta, rbf_num):
        w, e, b = RBFNet.__split_theta(theta, rbf_num, X.shape[1])
        err = RBFNet.h(X, theta, rbf_num) - y

        theta_w = theta.copy()
        theta_w[:rbf_num] = np.ones(rbf_num)
        w_der = err * RBFNet.h_split(X, theta_w, rbf_num)

        print(w_der.shape)

        # e_der = s * np.apply_along_axis(
        #     lambda x: - np.linalg.norm(x.T - b, axis=-1) ** 2 * w * np.exp(-e * np.linalg.norm(x.T - b, axis=-1) ** 2),
        #     arr=X, axis=1)
        # b_der = s * 3

    def train(self, X, y, theta0=None):
        m = X.shape[1]
        rbf_num = self.get_rbf_num()
        minimize = self.get_minimize()

        if not theta0:
            theta0 = np.random.uniform(-1, 1, size=(2 * rbf_num + rbf_num * m))

        opt_result = minimize(lambda theta: RBFNet.J(X, y, theta, rbf_num), x0=theta0)

        if not opt_result.success:
            raise RuntimeError('Method does not converged')

        theta_opt = opt_result.x

        w, e, b = RBFNet.__split_theta(X, self.get_rbf_num(), m)

        self.__set_w(w)
        self.__set_e(e)
        self.__set_b(b)
        self.set_theta(theta_opt)

    def fit(self, X):
        return RBFNet.h(X, self.get_theta(), self.get_rbf_num())
