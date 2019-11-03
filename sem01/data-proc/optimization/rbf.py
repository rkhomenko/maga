import numpy as np
from scipy.optimize import minimize
import matplotlib.pyplot as plt


def opt_grad(func, x0, alpha, eps):
    e = 1e-4
    xk = x0

    while True:
        print(xk)
        xk1 = xk - alpha * (func(xk + e) - func(xk - e)) / 2 / e

        if np.linalg.norm(xk1 - xk) < eps:
            break

        xk = xk1

    return xk1


def f(x):
    return (x - 1) ** 2 + 1


def h(x, theta, N):
    m = x.shape[0]

    w = theta[:N]
    e = theta[N:2 * N]
    b = theta[2 * N:2 * N + N * m].reshape((N, m))

    return w * np.exp(-e * np.linalg.norm(x.T - b, axis=-1))


def J(x, y, theta, N):
    return np.sum((h(x, theta, N) - y) ** 2 )


n = 5
m = 3
N = 2

x = np.ones(m)
y = np.array([1])
theta0 = np.random.uniform(0, 1, size=(2 * N + N * m))

j = J(x, y, theta0, N)
print('Error on random theta:', J(x, y, theta0, N))

opt_result = minimize(lambda theta: J(x, y, theta, N), x0=theta0, method='Nelder-Mead')
theta_o = opt_result.x
print('Theta opt:', theta_o)
print('Error on theta opt:', J(x, y, theta_o, N))
