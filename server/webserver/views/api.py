import os
from pathlib import Path

from flask import Blueprint
from flask_restful import Api

from webserver.definitions import ROOT_DIR
from webserver.util import LeaderboardManager, UserManager
from webserver.views.resources import Add, Remove

api_bp = Blueprint('api', __name__)
api = Api(api_bp)
leaderboard_manager = LeaderboardManager(
    Path(os.path.join(ROOT_DIR, './static/leaderboard.json')))
user_manager = UserManager(Path(os.path.join(ROOT_DIR, './static/users.json')))


@api_bp.route('/')
def route_api():
    """ Dismisses all requests to the /api route"""
    return "", 403


# register resources
api.add_resource(Add, '/add', resource_class_kwargs={
                 'leaderboard_manager': leaderboard_manager, 'user_manager': user_manager})
api.add_resource(Remove, '/remove', resource_class_kwargs={
                 'leaderboard_manager': leaderboard_manager, 'user_manager': user_manager})