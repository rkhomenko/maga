import numba as nb


#@nb.jit(nopython=True)
def haar(x):
    if 0 <= x < 0.5:
        return 1
    if 0.5 <= x < 1:
        return -1
    return 0


def get_wavelet(name: str):
    return {
        'haar': haar
    }[name]
