import json
from operator import itemgetter
from pathlib import Path
from datetime import date

from webserver.models import Leaderboard


class LeaderboardManager:
    """Manager class holding the leaderboard data, performs read and write operations from and to DB."""
    db = None

    def __init__(self, db):
        self.db = db

    def get_sorted_data(self) -> list:
        """Returns leaderboard data sorted by score, highest score = first index"""
        return [entry.serialize for entry in Leaderboard.query.order_by(Leaderboard.score.desc())]

    def add(self, name: str, score: int, time: date) -> None:
        """Adds a leaderboard entry to the leaderboard data and writes it to the disk"""
        entry = Leaderboard(name=name, score=score, time=time)
        self.db.session.add(entry)
        self.db.session.commit()

    def remove(self, name: str) -> None:
        """Removes all leaderboard entries with the provided name"""
        Leaderboard.query.filter_by(name=name).delete()
        self.db.session.commit()
