import json
from jsonschema import validate

# expected json schema of user file
json_schema = {
    "type": "array",
    "contains": {
        "type": "object",
        "properties": {
            "name": {"type": "string"},
            "password": {"type": "string"}

        }
    }
}


class UserManager:
    """ User manager for Basic API authentication, reads user data
    from a local JSON file, storing usernames and passwords for Basic auth user accounts"""

    # TODO: please don't store user passwords in plaintext

    path: str
    user_data: str

    def __init__(self, path="users.json"):
        self.path = path

        with open(path) as json_file:
            self.user_data = json.load(json_file)

        # validate json data
        validate(instance=self.user_data, schema=json_schema)

    def validate_user(self, username: str, password: str) -> bool:
        """ Quickly checks if user credentials are correct or not, please
        note that this method is insecure and should be rewritten to not work
        with plaintext passwords anymore."""

        user_valid = False

        for user in self.user_data:
            if user['name'] == username and user['password'] == password:
                user_valid = True
                break

        return user_valid
