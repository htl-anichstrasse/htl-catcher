from operator import itemgetter

from flask import Blueprint, render_template

from webserver.util.leaderboard import LeaderboardManager
from webserver.views.api import leaderboard_manager

home = Blueprint('home', __name__)


@home.route('/')
def route_home():
    """ Renders the default page of the webserver, the leaderboard display"""
    return render_template("home.html", data=sorted(leaderboard_manager.leaderboard_data, key=itemgetter('score'), reverse=True))


def add_entry(leaderboard_manager: LeaderboardManager):
    """ Fired when a new entry is added via the /add REST node"""
    # TODO: Implement method
    pass


def remove_entry(leaderboard_manager: LeaderboardManager):
    """ Fired when a entry is removed from the leaderboard via the /remove REST node"""
    # TODO: Implement method
    pass
