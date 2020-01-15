import json
import threading
from pathlib import Path

from jsonschema import validate
from operator import itemgetter

# expected json schema of leaderboard file
JSON_SCHEMA = {
    "type": "array",
    "contains": {
        "type": "object",
        "properties": {
            "name": {"type": "string"},
            "score": {"type": "number"},
            "message": {"type": "string"}
        },
        "required": ["name", "score"]
    }
}


class LeaderboardManager:
    """Manager class holding the leaderboard data. This class also manages access
    to the leaderboard JSON file."""

    path: Path
    leaderboard_data: list
    lock = threading.Lock()  # lock used for write operations on the file

    def __init__(self, path: Path):
        self.path = path

        if path.exists():
            with open(path.resolve()) as json_file:
                self.leaderboard_data = json.load(json_file)

            # validate json data
            if self.leaderboard_data != []:
                validate(instance=self.leaderboard_data, schema=JSON_SCHEMA)
        else:
            with open(path, "w+") as file:
                file.write("[]")
                self.leaderboard_data = []

    def write(self) -> None:
        """Writes the current version of `leaderboard_data` to the disk """
        with open(self.path, "w+") as file:
            json.dump(self.leaderboard_data, file, indent=4, sort_keys=True)

    def get_ranked_entry(self, position: int) -> dict:
        """Gets a ranked entry in the leaderboard by the provided position"""
        sorted_data = sorted(self.leaderboard_data, key=itemgetter('score'), reverse=True)
        return sorted_data[position]

    def add(self, name, score, message="") -> None:
        """Adds a leaderboard entry to the leaderboard data and writes it to the disk"""
        self.lock.acquire()
        self.leaderboard_data.append(
            {"name": name, "score": score, "message": message})  # append ram data

        # write changes to disk
        self.write()

        self.lock.release()

    def remove(self, name) -> None:
        """Removes all leaderboard entries with the provided name"""
        self.lock.acquire()
        self.leaderboard_data = [value for value in self.leaderboard_data if value['name'] != name]

        # write changes to disk
        self.write()

        self.lock.release()
