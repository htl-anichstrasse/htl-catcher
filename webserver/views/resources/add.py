from venv import logger

from flask import app
from flask_restful import Resource, reqparse, request

from webserver.util import LeaderboardManager, UserManager
from webserver.views.home import add_entry


class Add(Resource):
    """ REST resource for adding new leaderboard entries"""

    user_manager: UserManager
    leaderboard_manager: LeaderboardManager

    def __init__(self, **kwargs):
        self.leaderboard_manager = kwargs['leaderboard_manager']
        self.user_manager = kwargs['user_manager']

    def post(self):
        """ Processes a POST HTTP reques to this resource"""
        # process the request if the authentication header is valid
        self.user_manager.check(request.headers.get('Authorization'))
        # authorization successful, parse request ...
        parser = reqparse.RequestParser()
        # the player's (user)name
        parser.add_argument('name', type=str, required=True)
        # the player's reached score
        parser.add_argument('score', type=int, required=True)
        # players can append an optional message displayed on screen
        parser.add_argument('message', type=str)

        # add parsed information to leaderboard manager
        args = parser.parse_args()
        self.leaderboard_manager.add(
            args['name'], args['score'], args['message'] if args['message'] else "")

        # send event to front end
        add_entry(self.leaderboard_manager)

        # reply with success message
        return {'message': 'Successfully added new leaderboard entry.'}
