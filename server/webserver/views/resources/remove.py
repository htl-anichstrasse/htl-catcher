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
        self.user_manager.check(request.headers.get('Authorization'))
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
