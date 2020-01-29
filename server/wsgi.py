# wsgi start script for heroku deploy

from webserver.__main__ import app
from webserver.views import api_bp, home

if __name__ == "__main__":
    # register blueprints
    app.register_blueprint(home)
    app.register_blueprint(api_bp, url_prefix='/api')

    # set config
    app.config.from_pyfile('webserver/config.py', silent=True)

    # lets go
    app.run()
