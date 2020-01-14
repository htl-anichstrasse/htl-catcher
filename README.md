# HTL Catcher Leaderboard Server

This is the [Flask](http://flask.palletsprojects.com/en/1.1.x/) leaderboard server for use at HTL Anichstraße's open day 2020. There will be a tablet for use by visitors on the open day where they can submit their HTL Catcher high scores including a name and their avatar. The leaderboard server will receive submitted high scores through a REST node, save it locally and live-reload the page serving the high score list.

Using a projector and a computer at the open day, visitors will be able to see their high score pop up on the projector's image as soon as they click the submit button (if they made it in the top 10 ...)

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

3. Create a user account for the API. Add a `users.json` file to /static/users.json and add at least one user account using the following format:
```json
[
    {
        "name": "foo",
        "password_hash": "bar"
    }
]
```
You can create the salted hash for the `password_hash` field using the `generate_hash.py` script

```
py generate_hash.py <password>
```

4. Run the server module
```
python -m webserver
```

## How to use the API

If you are looking for the API documentation, please see the [README in webserver/views/resources](webserver/views/resources/README.md).

## License

The [license file](https://github.com/htl-anichstrasse/htl-catcher/blob/master/LICENSE) in the root directory of this repository applies.