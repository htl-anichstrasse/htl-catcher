from flask_restful import Resource, reqparse, request

from webserver.util import LeaderboardManager


class Fetch(Resource):
    """ REST resource for fetching leaderboard entries"""

    leaderboard_manager: LeaderboardManager

    def __init__(self, **kwargs):
        self.leaderboard_manager = kwargs['leaderboard_manager']

    def get(self):
        """ Processes a GET HTTP request to this resource"""
        # authorization successful, parse request ...
        parser = reqparse.RequestParser()
        # the player's (user)name
        parser.add_argument('position', type=int, required=True)

        # add parsed information to leaderboard manager
        args = parser.parse_args()

        # get position from leaderboard manager
        position = int(args['position'])

        if position < len(self.leaderboard_manager.leaderboard_data):
            entry = self.leaderboard_manager.get_ranked_entry(position)
        else:
            return {'message': 'Invalid position'}

        # reply with success message
        return entry
