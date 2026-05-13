from flask import Blueprint, request, jsonify, Response
from app.services.story_service import StoryService
from app.auth.jwt_handler import require_auth, require_role
from app.exceptions.custom_exceptions import NotFoundError, ValidationError, DuplicateError

story_v2_bp = Blueprint('story_v2', __name__)


class StoryControllerV2:
    def __init__(self, story_service):
        self.story_service = story_service
        self.register_routes()

    def register_routes(self):

        @story_v2_bp.route('', methods=['POST'])
        @require_auth
        def create_story():
            """Авторизованные пользователи могут создавать"""
            try:
                data = request.get_json()
                result = self.story_service.create(data)
                return jsonify(result.to_dict()), 201
            except DuplicateError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 403
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @story_v2_bp.route('', methods=['GET'])
        @require_auth
        def get_stories():
            """Все авторизованные могут читать"""
            result = self.story_service.get_all()
            items = result.get('items', result) if isinstance(result, dict) else result
            return jsonify([item.to_dict() if hasattr(item, 'to_dict') else item for item in items]), 200

        @story_v2_bp.route('/<int:id>', methods=['GET'])
        @require_auth
        def get_story(id):
            try:
                story = self.story_service.get_by_id(id)
                return jsonify(story.to_dict() if hasattr(story, 'to_dict') else story), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @story_v2_bp.route('/<int:id>', methods=['PUT'])
        @require_auth
        def update_story(id):
            try:
                data = request.get_json()
                result = self.story_service.update(id, data)
                return jsonify(result.to_dict()), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @story_v2_bp.route('/<int:id>', methods=['DELETE'])
        @require_auth
        def delete_story(id):
            """ADMIN может удалять все, CUSTOMER только свои stories"""
            user_role = request.current_user.get('role')
            user_login = request.current_user.get('login')

            story = self.story_service.repository.find_by_id(id)
            if not story:
                return jsonify({
                    "errorMessage": f"Story with id {id} not found",
                    "errorCode": "40401"
                }), 404

            # CUSTOMER может удалить только свою story
            if user_role != 'ADMIN':
                from app.models.writer_model import WriterModel
                writer = WriterModel.query.get(story.writer_id)
                if not writer or writer.login != user_login:
                    return jsonify({
                        "errorMessage": "Access denied. You can only delete your own stories.",
                        "errorCode": "40302"
                    }), 403

            try:
                self.story_service.delete(id)
                return Response(status=204)
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404