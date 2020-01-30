import binascii
import hashlib
import os
from typing import sys


def hash_password(password: str) -> str:
    """Hash a salt + password for storing."""
    salt = hashlib.sha256(os.urandom(60)).hexdigest().encode('ascii')
    pwdhash = hashlib.pbkdf2_hmac('sha512', password.encode('utf-8'),
                                  salt, 100000)
    pwdhash = binascii.hexlify(pwdhash)
    return (salt + pwdhash).decode('ascii')


if '__main__' == __name__:
    if (len(sys.argv) != 2):
        print('Please provide a password for hashing!')
        exit(-1)
    else:
        print(hash_password(sys.argv[1]))
        exit(0)
