from flask import Blueprint, request, jsonify
from app.services.mark_service import MarkService
from app.dto.requests.mark_request import MarkRequestTo
from app.exceptions.custom_exceptions import NotFoundError, ValidationError
from app.utils.case_converter import convert_keys_to_camel, convert_keys_to_snake

mark_bp = Blueprint('mark', __name__)


class MarkController:
    def __init__(self, mark_service: MarkService):
        self.mark_service = mark_service

    def _parse_request(self, data: dict) -> MarkRequestTo:
        snake_data = convert_keys_to_snake(data)
        return MarkRequestTo(**snake_data)

    def _to_response(self, data) -> dict:
        if hasattr(data, '__dict__'):
            result = data.__dict__
        else:
            result = data
        return convert_keys_to_camel(result)

    def register_routes(self):
        @mark_bp.route('', methods=['POST'])
        def create_mark():
            try:
                data = request.get_json()
                mark_request = self._parse_request(data)
                result = self.mark_service.create(mark_request)
                return jsonify(self._to_response(result)), 201
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @mark_bp.route('', methods=['GET'])
        def get_marks():
            marks = self.mark_service.get_all()
            return jsonify([self._to_response(m) for m in marks]), 200

        @mark_bp.route('/<int:id>', methods=['GET'])
        def get_mark(id):
            try:
                mark = self.mark_service.get_by_id(id)
                return jsonify(self._to_response(mark)), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @mark_bp.route('/<int:id>', methods=['PUT'])
        def update_mark(id):
            try:
                data = request.get_json()
                mark_request = self._parse_request(data)
                result = self.mark_service.update(id, mark_request)
                return jsonify(self._to_response(result)), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @mark_bp.route('/<int:id>', methods=['DELETE'])
        def delete_mark(id):
            try:
                self.mark_service.delete(id)
                return '', 204
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        return mark_bp