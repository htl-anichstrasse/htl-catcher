from flask import Blueprint, render_template

home = Blueprint('home', __name__)


@home.route('/')
def route_home():
    return "Hello world, this blueprint works!"
