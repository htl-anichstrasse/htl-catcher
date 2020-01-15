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
        parser.add_argument('position', type=int)

        # add parsed information to leaderboard manager
        args = parser.parse_args()

        # get sorted leaderboard data
        data = self.leaderboard_manager.get_sorted_data()

        # return all data or the requested position
        if args['position'] != None:
            position = int(args['position'])
            if position < len(data):
                entry = data[position]
            else:
                return {'message': 'Invalid position'}
        else:
            return data[:10]

        # reply with success message
        return entry
