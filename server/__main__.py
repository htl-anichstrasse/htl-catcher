from flask import Flask

from .views.api import api_bp
from .views.home import home

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
        return "", 404

    # run application
    app.run()