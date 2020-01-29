from pathlib import Path

from flask import Flask

from webserver.util import LeaderboardManager

from .config import *
from .definitions import *

# initialize flask app
app = Flask(__name__)

leaderboard_manager = LeaderboardManager(
    Path(os.path.join(ROOT_DIR, './static/leaderboard.json')))
