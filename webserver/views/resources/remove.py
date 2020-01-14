from venv import logger

from flask import app
from flask_restful import Resource, reqparse, request

from webserver.util import LeaderboardManager, UserManager
from webserver.views.home import remove_entry


class Remove(Resource):
    """ REST resource for removing leaderboard entries"""

    user_manager: UserManager
    leaderboard_manager: LeaderboardManager

    def __init__(self, **kwargs):
        self.leaderboard_manager = kwargs['leaderboard_manager']
        self.user_manager = kwargs['user_manager']

    def post(self):
        """ Processes a POST HTTP reques to this resource"""
        # process the request if the authentication header is valid
        authorization_header = request.headers.get('Authorization')
        if authorization_header and self.user_manager.check(authorization_header):
            # authorization successful, parse request ...
            parser = reqparse.RequestParser()
            # the name of the player to be removed from the leaderboard
            parser.add_argument('name', type=str, required=True)

            # add parsed information to leaderboard manager
            args = parser.parse_args()
            self.leaderboard_manager.remove(args['name'])

            # send event to front end
            remove_entry(self.leaderboard_manager)

            # reply with success message
            return {'message': 'Successfully removed leaderboard entry / entries.'}

        # authorization failed
        # abort, user is not authenticated
        return {'message': 'You need to be authorized to perform this action.'}, 401
