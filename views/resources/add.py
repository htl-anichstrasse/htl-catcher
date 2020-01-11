import base64
import os
import sys
from functools import wraps
from pathlib import Path

from flask_restful import Resource, reqparse, request

from ...users import UserManager
from ...leaderboard import LeaderboardManager


class add(Resource):

    user_manager = UserManager(Path('./server/static/users.json'))
    leaderboard: LeaderboardManager

    def __init__(self, leaderboard: LeaderboardManager):
        self.leaderboard = leaderboard

    def check(self, authorization_header: str) -> bool:
        """ Checks the authorization header using the user manager. Returns true
        if the user + password in the header is valid, false otherwise.

        Please note that the authorization must be encoded in Base64 as per RFC 2617"""

        decoded_username_password = base64.b64decode(
            authorization_header.split()[-1].encode('ascii')).decode('ascii')
        username_password_split = decoded_username_password.split(':')
        return self.user_manager.validate_user(username_password_split[0], username_password_split[1])

    def post(self):
        authorization_header = request.headers.get('Authorization')
        if authorization_header and self.check(authorization_header):
            return self.process_request()

        # authorization failed
        # abort, user is not authenticated
        return {'message': 'You need to be authorized to perform this action.'}, 401

    def process_request(self) -> dict:
        """Processes an incoming request to this resource by an authorized user, returns
        the response for this request."""

        parser = reqparse.RequestParser()
        parser.add_argument('name', type=str, required=True) # the player's (user)name
        parser.add_argument('score', type=int, required=True) # the player's reached score
        parser.add_argument('message', type=str) # players can append an optional message displayed on screen

        args = parser.parse_args()
        self.leaderboard.add(args['name'], args['score'], args['message'] if args['message'] else "")
        # TODO: send change to front end

        return {'message': 'Successfully added new leaderboard entry.'}
