from flask import Blueprint, request, jsonify, Response
from app.services.writer_service import WriterService
from app.auth.jwt_handler import require_auth, require_role
from app.exceptions.custom_exceptions import NotFoundError, ValidationError

writer_v2_bp = Blueprint('writer_v2', __name__)


class WriterControllerV2:
    def __init__(self, writer_service):
        self.writer_service = writer_service
        self.register_routes()

    def _check_ownership(self, writer_id: int) -> bool:
        """Проверка, что CUSTOMER имеет доступ только к своим данным"""
        if request.current_user.get('role') == 'ADMIN':
            return True
        return request.current_user.get('login') == self._get_writer_login(writer_id)

    def _get_writer_login(self, writer_id: int) -> str:
        """Получение логина по ID"""
        writer = self.writer_service.repository.find_by_id(writer_id)
        return writer.login if writer else None

    def register_routes(self):

        @writer_v2_bp.route('', methods=['POST'])
        @require_auth
        @require_role(['ADMIN'])
        def create_writer():
            """Только ADMIN может создавать пользователей"""
            try:
                data = request.get_json()
                result = self.writer_service.create(data)
                return jsonify(result.to_dict()), 201
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @writer_v2_bp.route('', methods=['GET'])
        @require_auth
        def get_writers():
            """Все авторизованные могут смотреть список"""
            result = self.writer_service.get_all()
            items = result.get('items', result) if isinstance(result, dict) else result
            return jsonify([w.to_dict() for w in items]), 200

        @writer_v2_bp.route('/<int:id>', methods=['GET'])
        @require_auth
        def get_writer(id):
            """CUSTOMER может смотреть только свой профиль"""
            if not self._check_ownership(id):
                return jsonify({
                    "errorMessage": "Access denied. You can only view your own profile.",
                    "errorCode": "40302"
                }), 403

            try:
                writer = self.writer_service.get_by_id(id)
                return jsonify(writer.to_dict()), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @writer_v2_bp.route('/<int:id>', methods=['PUT'])
        @require_auth
        def update_writer(id):
            """CUSTOMER может обновлять только свой профиль"""
            if not self._check_ownership(id):
                return jsonify({
                    "errorMessage": "Access denied.",
                    "errorCode": "40302"
                }), 403

            try:
                data = request.get_json()
                result = self.writer_service.update(id, data)
                return jsonify(result.to_dict()), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @writer_v2_bp.route('/<int:id>', methods=['DELETE'])
        @require_auth
        def delete_writer(id):
            """ADMIN может удалять всех, CUSTOMER только себя"""
            user_role = request.current_user.get('role')
            user_login = request.current_user.get('login')

            # CUSTOMER может удалить только свой профиль
            if user_role != 'ADMIN':
                writer = self.writer_service.repository.find_by_id(id)
                if not writer or writer.login != user_login:
                    return jsonify({
                        "errorMessage": "Access denied. You can only delete your own profile.",
                        "errorCode": "40302"
                    }), 403

            try:
                self.writer_service.delete(id)
                return Response(status=204)
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404