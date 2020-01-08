# HTL Catcher Leaderboard Server

This is the [Flask](http://flask.palletsprojects.com/en/1.1.x/) leaderboard server for use at HTL Anichstraße's open day 2020. There will be a tablet for use by visitors on the open day where they can submit their HTL Catcher high scores including a name and their avatar. The leaderboard server will receive submitted high scores through a REST node, save it locally and live-reload the page serving the high score list.

Using a projecter and a computer at the open day, visitors will be able to see their high score pop up on the projector's image as soon as they click the submit button (if they made it in the top 10 ...)

## Installation

1. Clone the library and navigate into the server directory

```
git clone https://github.com/htl-anichstrasse/htl-catcher.git
cd htl-catcher/server
```

2. Install required libraries from `requirements.txt` using [PIP](https://pypi.org/project/pip/)
```
pip install -r requirements.txt
```

3. Run the server module
```
cd ..
python -m server
```