import numpy as np
import scipy as sc
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


x = opt_grad(f, np.random.uniform(-5, 5), alpha=0.01, eps=1e-6)
print(x, f(x))
