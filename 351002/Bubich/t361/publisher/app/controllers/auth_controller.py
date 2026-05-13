from flask import Blueprint, request, jsonify
from app.services.auth_service import AuthService
from app.exceptions.custom_exceptions import ValidationError

auth_bp = Blueprint('auth', __name__)


class AuthController:
    def __init__(self, auth_service: AuthService):
        self.auth_service = auth_service
        self.register_routes()

    def register_routes(self):
        @auth_bp.route('/writers', methods=['POST'])
        def register():
            """Регистрация нового пользователя: POST /api/v2.0/writers"""
            try:
                data = request.get_json()
                result = self.auth_service.register(data)
                return jsonify(result), 201
            except ValidationError as e:
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), int(e.error_code[:3])

        @auth_bp.route('/login', methods=['POST'])
        def login():
            """Аутентификация: POST /api/v2.0/login"""
            try:
                data = request.get_json()
                login = data.get('login', '')
                password = data.get('password', '')
                result = self.auth_service.login(login, password)
                return jsonify(result), 200
            except ValidationError as e:
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), 401