from flask import Blueprint, render_template

from webserver import leaderboard_manager

home = Blueprint('home', __name__)


@home.route('/')
def route_home():
    """ Renders the default page of the webserver, the leaderboard display"""
    return render_template("home.html", data=leaderboard_manager.get_sorted_data())
