from pathlib import Path

from webserver.util import LeaderboardManager

from .config import *
from .definitions import *

leaderboard_manager = LeaderboardManager(
    Path(os.path.join(ROOT_DIR, './static/leaderboard.json')))
