import base64
import binascii
import hashlib
import json
from pathlib import Path

import flask_restful
from jsonschema import validate

# expected json schema of user file
JSON_SCHEMA = {
    "type": "array",
    "contains": {
        "type": "object",
        "properties": {
            "name": {"type": "string"},
            "password_hash": {"type": "string"}

        }
    }
}


class UserManager:
    """User manager for Basic API authentication, reads user data
    from a local JSON file, storing usernames and passwords for Basic auth user accounts"""

    path: Path
    user_data: str

    def __init__(self, path: Path):
        self.path = path

        with open(path.resolve()) as json_file:
            self.user_data = json.load(json_file)

        # validate json data
        validate(instance=self.user_data, schema=JSON_SCHEMA)

    def _verify_password(self, stored_password: str, provided_password: str) -> bool:
        """Verify a stored password against one provided by user, returns true if the passwords
        match, false otherwise"""
        salt = stored_password[:64]  # salt is first 64 chars
        stored_password = stored_password[64:]
        pwdhash = hashlib.pbkdf2_hmac('sha512',
                                      provided_password.encode('utf-8'),
                                      salt.encode('ascii'),
                                      100000)
        pwdhash = binascii.hexlify(pwdhash).decode('ascii')
        return pwdhash == stored_password

    def validate_user(self, username: str, password: str) -> bool:
        """Quickly checks if user credentials are correct or not, please
        note that this method is insecure and should be rewritten to not work
        with plaintext passwords anymore."""

        user_valid = False

        for user in self.user_data:
            if user['name'] == username and self._verify_password(user['password_hash'], password):
                user_valid = True
                break

        return user_valid

    def check(self, authorization_header: str) -> None:
        """ Checks the authorization header using the user manager. Aborts the current
        request if the authorization header is invalid.

        Please note that the authorization must be encoded in Base64 as per RFC 2617"""

        if authorization_header:
            decoded_username_password = base64.b64decode(
                authorization_header.split()[-1].encode('ascii')).decode('ascii')
            username_password_split = decoded_username_password.split(':')
            if self.validate_user(username_password_split[0], username_password_split[1]):
                return

        flask_restful.abort(
            401, message="You need to be authorized to perform this action.")
