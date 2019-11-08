import numpy as np
from scipy.optimize import minimize as sc_minimize


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
