import setuptools

with open("README.md", "r") as fh:
    long_description = fh.read()

setuptools.setup(
    name="htl-catcher-server-HTL-Anichstrasse",
    version="0.0.1",
    author="HTL Anichstraße",
    author_email="htlinn@tsn.at",
    description="The webserver for displaying leaderboard entries from the HTL Catcher Android app",
    long_description=long_description,
    long_description_content_type="text/markdown",
    url="https://github.com/htl-anichstrasse/htl-catcher",
    packages=setuptools.find_packages(),
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    python_requires='>=3.6',
)
