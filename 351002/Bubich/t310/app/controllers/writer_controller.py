from flask import Blueprint, request, jsonify
from app.services.writer_service import WriterService
from app.dto.requests.writer_request import WriterRequestTo
from app.exceptions.custom_exceptions import NotFoundError, ValidationError
from app.utils.case_converter import convert_keys_to_camel, convert_keys_to_snake

writer_bp = Blueprint('writer', __name__)


class WriterController:
    def __init__(self, writer_service: WriterService):
        self.writer_service = writer_service

    def _parse_request(self, data: dict) -> WriterRequestTo:
        snake_data = convert_keys_to_snake(data)
        return WriterRequestTo(**snake_data)

    def _to_response(self, data) -> dict:
        if hasattr(data, '__dict__'):
            result = data.__dict__
        else:
            result = data
        return convert_keys_to_camel(result)

    def register_routes(self):
        @writer_bp.route('', methods=['POST'])
        def create_writer():
            try:
                data = request.get_json()
                writer_request = self._parse_request(data)
                result = self.writer_service.create(writer_request)
                return jsonify(self._to_response(result)), 201
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @writer_bp.route('', methods=['GET'])
        def get_writers():
            writers = self.writer_service.get_all()
            return jsonify([self._to_response(w) for w in writers]), 200

        @writer_bp.route('/<int:id>', methods=['GET'])
        def get_writer(id):
            try:
                writer = self.writer_service.get_by_id(id)
                return jsonify(self._to_response(writer)), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @writer_bp.route('/<int:id>', methods=['PUT'])
        def update_writer(id):
            try:
                data = request.get_json()
                writer_request = self._parse_request(data)
                result = self.writer_service.update(id, writer_request)
                return jsonify(self._to_response(result)), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @writer_bp.route('/<int:id>', methods=['DELETE'])
        def delete_writer(id):
            try:
                self.writer_service.delete(id)
                return '', 204
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        return writer_bp