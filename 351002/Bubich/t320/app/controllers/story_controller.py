from flask import Blueprint, request, jsonify
from app.services.story_service import StoryService
from app.dto.requests.story_request import StoryRequestTo
from app.exceptions.custom_exceptions import NotFoundError, ValidationError
from app.utils.case_converter import convert_keys_to_camel, convert_keys_to_snake

story_bp = Blueprint('story', __name__)


class StoryController:
    def __init__(self, story_service: StoryService):
        self.story_service = story_service

    def _parse_request(self, data: dict) -> StoryRequestTo:
        """Преобразует camelCase в snake_case и создает DTO"""
        # Конвертируем ключи из camelCase в snake_case
        snake_data = convert_keys_to_snake(data)
        return StoryRequestTo(**snake_data)

    def _to_response(self, data) -> dict:
        """Преобразует ответ в camelCase"""
        if hasattr(data, '__dict__'):
            result = data.__dict__
        else:
            result = data
        return convert_keys_to_camel(result)

    def register_routes(self):
        @story_bp.route('', methods=['POST'])
        def create_story():
            try:
                data = request.get_json()
                story_request = self._parse_request(data)
                result = self.story_service.create(story_request)
                return jsonify(self._to_response(result)), 201
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @story_bp.route('', methods=['GET'])
        def get_stories():
            # Поддержка фильтрации
            mark_names = request.args.getlist('markNames')
            mark_ids = request.args.getlist('markIds', type=int)
            writer_login = request.args.get('writerLogin')
            title = request.args.get('title')
            content = request.args.get('content')

            if any([mark_names, mark_ids, writer_login, title, content]):
                stories = self.story_service.get_by_criteria(
                    mark_names=mark_names if mark_names else None,
                    mark_ids=mark_ids if mark_ids else None,
                    writer_login=writer_login,
                    title=title,
                    content=content
                )
            else:
                stories = self.story_service.get_all()

            return jsonify([self._to_response(s) for s in stories]), 200

        @story_bp.route('/<int:id>', methods=['GET'])
        def get_story(id):
            try:
                story = self.story_service.get_by_id(id)
                return jsonify(self._to_response(story)), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        @story_bp.route('/<int:id>', methods=['PUT'])
        def update_story(id):
            try:
                data = request.get_json()
                story_request = self._parse_request(data)
                result = self.story_service.update(id, story_request)
                return jsonify(self._to_response(result)), 200
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404
            except ValidationError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 400

        @story_bp.route('/<int:id>', methods=['DELETE'])
        def delete_story(id):
            try:
                self.story_service.delete(id)
                return '', 204
            except NotFoundError as e:
                return jsonify({"errorMessage": e.message, "errorCode": e.error_code}), 404

        return story_bp