import setuptools

setuptools.setup(
    name="wavelets",
    version="1.0.0",
    author="Roman Khomenko",
    author_email="romankhomenko1995@gmail.com",
    description="Wavelets for ML",
    packages=setuptools.find_packages(),
    classifiers=[
        "Programming Language :: Python :: 3",
        "Operating System :: OS Independent",
    ],
    test_suit="tests",
    python_requires='>=3.7',
)