from flask import Blueprint

from flask_restful import Api

from .resources.add import add

api_bp = Blueprint('api', __name__)
api = Api(api_bp)


@api_bp.route('/')
def route_api():
    return "", 403


# register resources
api.add_resource(add, '/add')
