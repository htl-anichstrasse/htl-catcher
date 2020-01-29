# heroku entry point for gunicorn
from webserver.__main__ import app

if __name__ == "__main__":
    app.run()