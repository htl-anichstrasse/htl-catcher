from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import Column, DateTime, Integer, String

# database
db = SQLAlchemy()


class Leaderboard(db.Model):
    """Extremely simple database model for storing leaderboard data."""

    id = Column(Integer, primary_key=True)
    score = Column(Integer)
    name = Column(String)
    time = Column(DateTime)

    @property
    def serialize(self):
        return {'score': self.score, 'name': self.name, 'time': self.time}
