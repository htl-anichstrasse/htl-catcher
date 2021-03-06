import os
from pathlib import Path

from flask import Blueprint
from flask_restful import Api

from webserver import leaderboard_manager
from webserver.definitions import ROOT_DIR
from webserver.util import UserManager
from webserver.views.resources import Add, Fetch, Remove

api_bp = Blueprint('api', __name__)
api = Api(api_bp)
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
api.add_resource(
    Fetch, '/fetch', resource_class_kwargs={'leaderboard_manager': leaderboard_manager})
