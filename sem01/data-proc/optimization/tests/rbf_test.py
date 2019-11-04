import unittest
import rbf
import numpy as np


class RBFNetTestCase(unittest.TestCase):
    def test_net_fit(self):
        X = np.linspace(0, 10, num=100).reshape((100, 1))
        y = np.exp(-(X - 5) ** 2 / 2)

        n = 3
        net = rbf.RBFNet(rbf_num=n)
        w = np.array([0, 1, 0])
        e = np.array([1, 0.5, 1])
        b = np.array([0, 5, 0])
        net.set_theta(np.hstack((w, e, b)))

        self.assertEqual(np.array_equal(y, net.fit(X)), True)


if __name__ == '__main__':
    unittest.main()
