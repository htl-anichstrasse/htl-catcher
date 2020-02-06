# HTL Catcher Leaderboard Server

This is the [Flask](http://flask.palletsprojects.com/en/1.1.x/) leaderboard server for use at HTL Anichstraße's open day 2020. There will be a tablet for use by visitors on the open day where they can submit their HTL Catcher high scores including their name and a message. The leaderboard server will receive submitted high scores through a REST node, save it locally and live-reload the page using AJAX serving the high score list.

Using a projector and a computer at the open day, visitors will be able to see their high score pop up on the projector's image as soon as they tap the submit button (if they made it in the top 10 ...)

Please note that the Play Store version of the app will never be able to send leaderboard data accross the internet to a remote server. This feature is exclusively for the demonstration version on the open day. The Play Store version gets a local leaderboard instead.

You can see a live demo of the website on [catcher.htl.codes](https://catcher.htl.codes/).

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
Also, if you get an error that says that `mysql.h` or `config-win.h` is missing, please install `wheel`, download the newest version of `mysqlclient` [here](https://www.lfd.uci.edu/~gohlke/pythonlibs/#mysql-python) and install the WHL file.
```
pip install wheel
```
```
pip install mysqlclient‑1.4.6‑cp38‑cp38‑win32.whl
```

3. Make sure to set the environment variable for your database URL in the format `<dbmstype>://<username>:<password>@host/@database`
```console
user@pc:~$ $env:DATABASE_URL="mysql://root@password@127.0.0.1/catcher"
```

4. Create a user account for the API. Add a `users.json` file to /static/users.json and add at least one user account using the following format:
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

5. Run the server module
```
python -m webserver
```

## Built with

The webserver uses multiple different libraries and tools in order to work correctly. Thanks to every open source developer who made this project possible with their generous work!

- [Flask](https://github.com/pallets/flask)
- [flask-restful](https://github.com/flask-restful/flask-restful)
- [jsonschema](https://github.com/Julian/jsonschema)
- [jQuery](https://github.com/jquery/jquery)
- [Bootstrap](https://github.com/twbs/bootstrap)
- [zdog](https://github.com/metafizzy/zdog)

## How to use the API

If you are looking for the API documentation, please see the [README in webserver/views/resources](webserver/views/resources/README.md).

## License

The [license file](https://github.com/htl-anichstrasse/htl-catcher/blob/master/LICENSE) in the root directory of this repository applies.