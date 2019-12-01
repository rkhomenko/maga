from .mother import get_wavelet

import numpy as np
import numba as nb


def _calc_c(f, t, j, k, wavelet):
    a = 2 ** (j / 2.0)
    b = 2 ** j

    s = 0
    for i in range(f.shape[0]):
        s += f[i] * wavelet(b * t[i] - k)

    return a * s


def _dwt(f, t, wavelet, n, m):
    c = np.zeros((n, m))
    for j in range(1, n + 1):
        for k in range(0, m):
            c[j - 1, k] = _calc_c(f, t, j, k, wavelet)
    return c


def _calc_func(c, t, wavelet):
    f = 0
    for j in range(1, c.shape[0] + 1):
        for k in range(0, c.shape[1]):
            f += c[j - 1, k] * (2 ** (j / 2.0)) * wavelet((2 ** j) * t - k)
    return f


def _idwt(c, t, wavelet):
    f = np.zeros(t.shape[0])
    for i in range(t.shape[0]):
        f[i] = _calc_func(c, t[i], wavelet)
    return f


def dwt(f, t, wavelet, n, m):
    wfunc = get_wavelet(wavelet)
    return _dwt(f, t, wfunc, n, m)


def idwt(c, t, wavelet):
    wfunc = get_wavelet(wavelet)
    return _idwt(c, t, wfunc)
