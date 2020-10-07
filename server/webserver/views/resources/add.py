from datetime import date

from flask_restful import Resource, reqparse, request
from webserver.util import LeaderboardManager, UserManager


class Add(Resource):
    """ REST resource for adding new leaderboard entries"""

    user_manager: UserManager
    leaderboard_manager: LeaderboardManager

    def __init__(self, **kwargs):
        self.leaderboard_manager = kwargs['leaderboard_manager']
        self.user_manager = kwargs['user_manager']

    def post(self):
        """ Processes a POST HTTP request to this resource"""
        # process the request if the authentication header is valid
        self.user_manager.check(request.headers.get('Authorization'))
        # authorization successful, parse request ...
        parser = reqparse.RequestParser()
        # the player's (user)name
        parser.add_argument('name', type=str, required=True)
        # the player's reached score
        parser.add_argument('score', type=int, required=True)

        # add parsed information to leaderboard manager
        args = parser.parse_args()
        self.leaderboard_manager.add(args['name'], args['score'])

        # reply with success message
        return {'message': 'Successfully added new leaderboard entry.'}
