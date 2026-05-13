from flask import Blueprint, request, jsonify, Response
from app.services.writer_service import WriterService
from app.exceptions.custom_exceptions import NotFoundError, ValidationError, DuplicateError

writer_bp = Blueprint('writer', __name__)


class WriterController:
    def __init__(self, writer_service: WriterService):
        self.writer_service = writer_service
        self.register_routes()

    def register_routes(self):
        @writer_bp.route('', methods=['POST'])
        def create_writer():
            try:
                data = request.get_json()
                if not data:
                    return jsonify({"errorMessage": "Request body is required", "errorCode": "40001"}), 400
                result = self.writer_service.create(data)
                return jsonify(result.to_dict()), 201
            except DuplicateError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 403
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @writer_bp.route('', methods=['GET'])
        def get_writers():
            result = self.writer_service.get_all()
            items = result.get('items', result) if isinstance(result, dict) else result
            return jsonify([w.to_dict() for w in items]), 200

        @writer_bp.route('/<int:id>', methods=['GET'])
        def get_writer(id):
            try:
                writer = self.writer_service.get_by_id(id)
                return jsonify(writer.to_dict()), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @writer_bp.route('/<int:id>', methods=['PUT'])
        def update_writer(id):
            try:
                data = request.get_json()
                result = self.writer_service.update(id, data)
                return jsonify(result.to_dict()), 200
            except DuplicateError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 403
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @writer_bp.route('/<int:id>', methods=['DELETE'])
        def delete_writer(id):
            try:
                self.writer_service.delete(id)
                return Response(status=204)
            except NotFoundError as e:
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), 404
            except Exception as e:
                return jsonify({
                    "errorMessage": f"Error deleting writer: {str(e)}",
                    "errorCode": "50001"
                }), 500