import os
import sys
from copyreg import add_extension

from flask import Flask

from webserver.definitions import ROOT_DIR
from webserver.util import LeaderboardManager
from webserver.views import api_bp, home

# fix import
sys.path.insert(0, ROOT_DIR)


if __name__ == '__main__':
    app = Flask(__name__)

    # register blueprints
    app.register_blueprint(home)
    app.register_blueprint(api_bp, url_prefix='/api')

    # set config
    app.config.from_pyfile('config.py', silent=True)

    # handle errors
    @app.errorhandler(404)
    def page_not_found(error):
        """ Handles 404 errors by simply just returning the code and no page rendering"""
        return "", 404

    # run application
    app.run()
