import sys

import click
from flask import Flask
from flask.cli import with_appcontext

from webserver.definitions import ROOT_DIR
from webserver.models import db
from webserver.views import api_bp, home

# fix import
sys.path.insert(0, ROOT_DIR)

# initialize flask app
app = Flask(__name__)

# handle errors
@app.errorhandler(404)
def page_not_found(error):
    """ Handles 404 errors by simply just returning the code and no page rendering"""
    return "", 404

# database setup command
@click.command(name="create_tables")
@with_appcontext
def create_tables():
    db.create_all()


    # start the app if in main module
if __name__ == '__main__':
    # register blueprints
    app.register_blueprint(home)
    app.register_blueprint(api_bp, url_prefix='/api')

    # set config
    app.config.from_pyfile('config.py', silent=True)

    # register setup command
    app.cli.add_command(create_tables)

    # initialize postgres
    db.init_app(app)

    # run application
    app.run()
