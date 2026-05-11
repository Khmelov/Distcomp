from flask import Blueprint, request, jsonify, Response
from app.services.story_service import StoryService
from app.exceptions.custom_exceptions import NotFoundError, ValidationError, DuplicateError

story_bp = Blueprint('story', __name__)


class StoryController:
    def __init__(self, story_service: StoryService):
        self.story_service = story_service
        self.register_routes()

    def register_routes(self):
        @story_bp.route('', methods=['POST'])
        def create_story():
            try:
                data = request.get_json()
                if not data:
                    return jsonify({"errorMessage": "Request body is required", "errorCode": "40001"}), 400
                result = self.story_service.create(data)
                return jsonify(result.to_dict()), 201
            except DuplicateError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 403
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @story_bp.route('', methods=['GET'])
        def get_stories():
            try:
                result = self.story_service.get_all()
                items = result.get('items', result) if isinstance(result, dict) else result
                return jsonify([item.to_dict() if hasattr(item, 'to_dict') else item for item in items]), 200
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @story_bp.route('/<int:id>', methods=['GET'])
        def get_story(id):
            try:
                story = self.story_service.get_by_id(id)
                if hasattr(story, 'to_dict'):
                    return jsonify(story.to_dict()), 200
                return jsonify(story), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @story_bp.route('/<int:id>', methods=['PUT'])
        def update_story(id):
            try:
                data = request.get_json()
                result = self.story_service.update(id, data)
                return jsonify(result.to_dict()), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @story_bp.route('/<int:id>', methods=['DELETE'])
        def delete_story(id):
            try:
                self.story_service.delete(id)
                return Response(status=204)
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404