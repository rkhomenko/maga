import wavelets as wl

import numpy as np
import unittest


class TransformTest(unittest.TestCase):
    def test_transform(self):
        t = np.linspace(0, 2 * np.pi, 10)
        f = np.sin(t)

        n = 10
        m = 10

        c = wl.dwt(f, t, 'haar', n, m)
        ft = wl.idwt(c, t, 'haar')

        self.assertEqual(f, ft)


if __name__ == '__main__':
    unittest.main()
