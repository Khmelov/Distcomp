from flask import Blueprint, request, jsonify, Response
from app.services.story_service_db import StoryServiceDB
from app.utils.query_utils import get_pagination_params
from app.utils.case_converter import convert_keys_to_camel
from publisher.app.exceptions import NotFoundError, ValidationError, DuplicateError
story_bp = Blueprint('story_v2', __name__)


class StoryControllerDB:
    def __init__(self, story_service: StoryServiceDB):
        self.story_service = story_service
        self.register_routes()  # Регистрируем маршруты при создании

    def _convert_request_data(self, data: dict) -> dict:
        """Преобразует camelCase ключи в snake_case"""
        converted = {}
        field_mapping = {
            'writerId': 'writer_id',
            'writer_id': 'writer_id',
            'title': 'title',
            'content': 'content'
        }
        for key, value in data.items():
            if key in field_mapping:
                converted[field_mapping[key]] = value
            else:
                converted[key] = value
        return converted

    def register_routes(self):
        """Регистрация маршрутов для story"""

        @story_bp.route('', methods=['POST'])
        def create_story():
            try:
                data = request.get_json()
                if not data:
                    return jsonify({
                        "errorMessage": "Request body is required",
                        "errorCode": "40001"
                    }), 400

                converted_data = self._convert_request_data(data)
                result = self.story_service.create(converted_data)
                return jsonify(convert_keys_to_camel(result.to_dict())), 201

            except DuplicateError as e:  # ← ПЕРВЫМ должен быть DuplicateError!
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), 403
            except ValidationError as e:
                print(f"ValidationError: code={e.error_code}, message={e.message}")
                if e.error_code.startswith('403'):
                    return jsonify({
                        "errorMessage": e.message,
                        "errorCode": e.error_code
                    }), 403
                else:
                    return jsonify({
                        "errorMessage": e.message,
                        "errorCode": e.error_code
                    }), 400
            except Exception as e:  # ← Exception должен быть ПОСЛЕДНИМ!
                import traceback
                traceback.print_exc()
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40000"
                }), 400
        @story_bp.route('', methods=['GET'])
        def get_stories():
            try:
                pagination = get_pagination_params()
                filters = {
                    'writerId': request.args.get('writerId', type=int),
                    'title': request.args.get('title'),
                    'content': request.args.get('content'),
                    'writerLogin': request.args.get('writerLogin')
                }

                if any(filters.values()):
                    result = self.story_service.get_by_criteria(filters, pagination)
                else:
                    result = self.story_service.get_all(pagination)

                items = result.get('items', result) if isinstance(result, dict) else result
                response = [convert_keys_to_camel(item.to_dict()) for item in items]
                return jsonify(response), 200
            except Exception as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40000"
                }), 400

        @story_bp.route('/<int:id>', methods=['GET'])
        def get_story(id):
            try:
                story = self.story_service.get_by_id(id)
                return jsonify(convert_keys_to_camel(story.to_dict())), 200
            except NotFoundError as e:
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), 404

        @story_bp.route('/<int:id>', methods=['PUT'])
        def update_story(id):
            try:
                data = request.get_json()
                if not data:
                    return jsonify({
                        "errorMessage": "Request body is required",
                        "errorCode": "40001"
                    }), 400

                converted_data = self._convert_request_data(data)
                result = self.story_service.update(id, converted_data)
                return jsonify(convert_keys_to_camel(result.to_dict())), 200

            except NotFoundError as e:
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), 404
            except ValidationError as e:
                status_code = 403 if e.error_code.startswith('403') else 400
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), status_code
            except Exception as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40000"
                }), 400

        @story_bp.route('/<int:id>', methods=['DELETE'])
        def delete_story(id):
            try:
                self.story_service.delete(id)
                return Response(status=204)
            except NotFoundError as e:
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), 404


# Функция для получения blueprint (для совместимости)
def get_story_bp():
    return story_bp