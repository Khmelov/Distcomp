from flask import Blueprint, request, jsonify
from app.services.comment_service import CommentService
from app.dto.requests.comment_request import CommentRequestTo
from app.exceptions.custom_exceptions import NotFoundError, ValidationError
from app.utils.case_converter import convert_keys_to_camel, convert_keys_to_snake

comment_bp = Blueprint('comment', __name__)


class CommentController:
    def __init__(self, comment_service: CommentService):
        self.comment_service = comment_service

    def _parse_request(self, data: dict) -> CommentRequestTo:
        snake_data = convert_keys_to_snake(data)
        return CommentRequestTo(**snake_data)

    def _to_response(self, data) -> dict:
        if hasattr(data, '__dict__'):
            result = data.__dict__
        else:
            result = data
        return convert_keys_to_camel(result)

    def register_routes(self):
        @comment_bp.route('', methods=['POST'])
        def create_comment():
            try:
                data = request.get_json()
                comment_request = self._parse_request(data)
                result = self.comment_service.create(comment_request)
                return jsonify(self._to_response(result)), 201
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @comment_bp.route('', methods=['GET'])
        def get_comments():
            story_id = request.args.get('storyId', type=int)
            if story_id:
                comments = self.comment_service.get_by_story_id(story_id)
            else:
                comments = self.comment_service.get_all()
            return jsonify([self._to_response(c) for c in comments]), 200

        @comment_bp.route('/<int:id>', methods=['GET'])
        def get_comment(id):
            try:
                comment = self.comment_service.get_by_id(id)
                return jsonify(self._to_response(comment)), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @comment_bp.route('/<int:id>', methods=['PUT'])
        def update_comment(id):
            try:
                data = request.get_json()
                comment_request = self._parse_request(data)
                result = self.comment_service.update(id, comment_request)
                return jsonify(self._to_response(result)), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @comment_bp.route('/<int:id>', methods=['DELETE'])
        def delete_comment(id):
            try:
                self.comment_service.delete(id)
                return '', 204
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        return comment_bp