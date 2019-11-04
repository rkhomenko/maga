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
    def __h_helper(x, w, e, b):
        return w * np.exp(-e * np.linalg.norm(x.T - b, axis=-1) ** 2)

    @staticmethod
    def h_split(X, theta, rbf_num):
        m = X.shape[1]
        w = theta[:rbf_num]
        e = theta[rbf_num:2 * rbf_num]
        b = theta[2 * rbf_num:2 * rbf_num + rbf_num * m].reshape((rbf_num, m))

        return np.apply_along_axis(lambda x: RBFNet.__h_helper(x, w, e, b), arr=X, axis=1)

    @staticmethod
    def h(X, theta, rbf_num):
        h_row_wise = np.sum(RBFNet.h_split(X, theta, rbf_num), axis=1)
        return h_row_wise.reshape((X.shape[0], X.shape[1]))

    @staticmethod
    def J(X, y, theta, rbf_num, reg=0.1):
        return np.sum((RBFNet.h(X, theta, rbf_num) - y) ** 2) / 2

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

        self.__set_w(theta_opt[:rbf_num])
        self.__set_e(theta_opt[rbf_num:2 * rbf_num])
        self.__set_b(theta_opt[2 * rbf_num:].reshape((rbf_num, m)))
        self.set_theta(theta_opt)

    def fit(self, X):
        return RBFNet.h(X, self.get_theta(), self.get_rbf_num())
