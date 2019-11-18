import numpy
import matplotlib.pylab as plt
import math


def f(x):
    return 3.0 * x[0] ** 2 + 2.0 * x[0] * x[1] + x[1] ** 2 - 4.0 * x[0] + 5.0 * x[1]


def grad(x):
    return numpy.array([6.0 * x[0] + 2.0 * x[1] - 4.0,
                        2.0 * x[0] + 2.0 * x[1] + 5.0])


x1 = numpy.linspace(-25, 25, 400)
x2 = numpy.linspace(-25, 25, 400)
X, Y = numpy.meshgrid(x1, x2)
z = f([X, Y])

fig = plt.figure()
ax = fig.add_subplot(1, 1, 1)
plt.pcolormesh(X, Y, z, cmap='Blues')
plt.colorbar()
plt.contour(X, Y, z, colors="black", alpha=0.5, linestyles="dotted", linewidth=0.5)


def line_search_interpolation(g, h, a0, p):
    deriv = numpy.dot(g, h)

    e0 = f(p)
    print("%12.8f  %12.8f  %12.8f" % (e0, p[0], p[1]))

    e1 = f(p + a0 * h)
    p1 = p + a0 * h
    print("%12.8f  %12.8f  %12.8f  %12.8f" % (e1, p1[0], p1[1], a0))

    if e1 < e0 + 1e-4 * a0 * deriv:
        return a0

    a1 = -deriv * a0 * a0 / (2.0 * (e1 - e0 - deriv * a0))
    e2 = f(p + a1 * h)
    p2 = p + a1 * h
    print("%12.8f  %12.8f  %12.8f  %12.8f" % (e2, p2[0], p2[1], a1))

    if e2 < e0 + 1e-4 * a1 * deriv:
        return a1

    a = 1.0 / (a0 * a0 * a1 * a1 * (a1 - a0)) * (a0 * a0 * (e2 - e0 - deriv * a1) - a1 * a1 * (e1 - e0 - deriv * a0))
    b = 1.0 / (a0 * a0 * a1 * a1 * (a1 - a0)) * (
                -a0 * a0 * a0 * (e2 - e0 - deriv * a1) + a1 * a1 * a1 * (e1 - e0 - deriv * a0))
    a2 = -b + math.sqrt(b * b - 3.0 * a * deriv) / (3.0 * a)
    if a2 < 0:
        a2 = a1 / 2.0
    e3 = f(p + a2 * h)
    p3 = p + a2 * h

    print("%12.8f  %12.8f  %12.8f  %12.8f" % (e3, p3[0], p3[1], a2))

    if e3 < e0 + 1e-4 * a2 * deriv:
        return a2

    return 0.0


def conjugate_gradient(p, color):
    iter = 0
    g = grad(p)
    h = -g / numpy.linalg.norm(g)
    eold = f(p)
    pold = numpy.copy(p)
    maxlinesearch = 2.0

    while (iter < 100):
        a = line_search_interpolation(g, h, maxlinesearch, p)
        mp = p + maxlinesearch * h

        g1 = grad(p + a * h)
        angle = math.acos(numpy.dot(-g1, h) / (numpy.linalg.norm(g1) * numpy.linalg.norm(h)))
        print("%12.8f" % angle)

        p += a * h

        enew = f(p)
        dif = math.fabs(enew - eold)
        if dif < 1e-4:
            print("Done")
            break

        eold = enew

        if angle > (math.pi / 4):
            print("Angle larger than 45 deg, perform conjugate gradient step")

            oldg = numpy.copy(g)
            g = grad(p)
            beta = numpy.dot(g, g - oldg) / numpy.dot(oldg, oldg)
            h = -g + beta * h

            plt.plot([pold[0], p[0]], [pold[1], p[1]], linestyle="dashed", linewidth=2, color=color)

            pold = numpy.copy(p)

        iter += 1


conjugate_gradient([20, 20], 'red')
conjugate_gradient([-20, 20], 'green')
conjugate_gradient([-20, -20], 'blue')
conjugate_gradient([20, -20], 'black')

plt.show()
