import base64
import os
import sys
from functools import wraps
from pathlib import Path

from flask_restful import Resource, request

from ...users import UserManager

user_manager = UserManager(os.path.join(
    str(Path(os.path.dirname(os.path.abspath(__file__))).parents[1]), "users.json"))


def check(authorization_header: str) -> bool:
    """ Checks the authorization header using the user manager. Returns true
     if the user + password in the header is valid, false otherwise.

    Please note that the authorization must be encoded in Base64 as per RFC 2617"""

    decoded_username_password = base64.b64decode(
        authorization_header.split()[-1].encode('ascii')).decode('ascii')
    username_password_split = decoded_username_password.split(':')
    return user_manager.validate_user(username_password_split[0], username_password_split[1])


class add(Resource):
    def post(self):
        authorization_header = request.headers.get('Authorization')
        if authorization_header and check(authorization_header):
            return {'message': 'Authorized oida'}

        # authorization failed
        return {'message': 'You need to be authorized to perform this action.'}, 401  # abort, user is not authenticated
