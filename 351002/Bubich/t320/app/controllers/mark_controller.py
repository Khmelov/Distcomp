from flask import Blueprint, request, jsonify, Response
from app.services.mark_service import MarkService
from app.exceptions.custom_exceptions import NotFoundError, ValidationError

mark_bp = Blueprint('mark', __name__)


class MarkController:
    def __init__(self, mark_service: MarkService):
        self.mark_service = mark_service
        self.register_routes()

    def register_routes(self):
        @mark_bp.route('', methods=['POST'])
        def create_mark():
            try:
                data = request.get_json()
                result = self.mark_service.create(data)
                return jsonify(result.to_dict()), 201
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @mark_bp.route('', methods=['GET'])
        def get_marks():
            result = self.mark_service.get_all()
            items = result.get('items', result) if isinstance(result, dict) else result
            return jsonify([m.to_dict() for m in items]), 200

        @mark_bp.route('/<int:id>', methods=['GET'])
        def get_mark(id):
            try:
                mark = self.mark_service.get_by_id(id)
                return jsonify(mark.to_dict()), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @mark_bp.route('/<int:id>', methods=['PUT'])
        def update_mark(id):
            try:
                data = request.get_json()
                result = self.mark_service.update(id, data)
                return jsonify(result.to_dict()), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @mark_bp.route('/<int:id>', methods=['DELETE'])
        def delete_mark(id):
            try:
                self.mark_service.delete(id)
                return Response(status=204)
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404