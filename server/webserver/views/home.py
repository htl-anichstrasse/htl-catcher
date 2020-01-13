from flask import Blueprint, render_template

from webserver.util.leaderboard import LeaderboardManager

home = Blueprint('home', __name__)


@home.route('/')
def route_home():
    """ Renders the default page of the webserver, the leaderboard display"""
    return render_template("home.html")


def add_entry(leaderboard_manager: LeaderboardManager):
    """ Fired when a new entry is added via the /add REST node"""
    # TODO: Implement method
    pass
