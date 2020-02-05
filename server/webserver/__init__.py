from pathlib import Path

from .config import *
from .definitions import *
from .models import *

from webserver.util import LeaderboardManager

# leaderboard
leaderboard_manager = LeaderboardManager(db)