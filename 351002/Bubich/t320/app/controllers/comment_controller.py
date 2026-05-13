from flask import Blueprint, request, jsonify, Response
from app.services.comment_service import CommentService
from app.exceptions.custom_exceptions import NotFoundError, ValidationError

comment_bp = Blueprint('comment', __name__)


class CommentController:
    def __init__(self, comment_service: CommentService):
        self.comment_service = comment_service
        self.register_routes()

    def _convert_request_data(self, data: dict) -> dict:
        """Преобразует camelCase ключи в snake_case"""
        converted = {}
        field_mapping = {
            'storyId': 'story_id',
            'story_id': 'story_id',
            'content': 'content'
        }
        for key, value in data.items():
            if key in field_mapping:
                converted[field_mapping[key]] = value
            else:
                converted[key] = value
        return converted

    def register_routes(self):
        @comment_bp.route('', methods=['POST'])
        def create_comment():
            try:
                data = request.get_json()
                if not data:
                    return jsonify({
                        "errorMessage": "Request body is required",
                        "errorCode": "40001"
                    }), 400

                # Преобразуем camelCase в snake_case
                converted_data = self._convert_request_data(data)
                result = self.comment_service.create(converted_data)

                # Возвращаем ответ в camelCase
                response_dict = result.to_dict()
                # Убеждаемся, что ответ в camelCase
                if 'story_id' in response_dict:
                    response_dict['storyId'] = response_dict.pop('story_id')

                return jsonify(response_dict), 201

            except ValidationError as e:
                if e.error_code.startswith('403'):
                    return jsonify({
                        "errorMessage": e.message,
                        "errorCode": e.error_code
                    }), 403
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), 400
            except Exception as e:
                import traceback
                traceback.print_exc()
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40000"
                }), 400

        @comment_bp.route('', methods=['GET'])
        def get_comments():
            try:
                story_id = request.args.get('storyId', type=int)
                if story_id:
                    comments = self.comment_service.get_by_story_id(story_id)
                else:
                    result = self.comment_service.get_all()
                    comments = result.get('items', result) if isinstance(result, dict) else result

                # Преобразуем ответы в camelCase
                response_list = []
                for c in comments:
                    c_dict = c.to_dict()
                    if 'story_id' in c_dict:
                        c_dict['storyId'] = c_dict.pop('story_id')
                    response_list.append(c_dict)

                return jsonify(response_list), 200
            except Exception as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40000"
                }), 400

        @comment_bp.route('/<int:id>', methods=['GET'])
        def get_comment(id):
            try:
                comment = self.comment_service.get_by_id(id)
                c_dict = comment.to_dict()
                if 'story_id' in c_dict:
                    c_dict['storyId'] = c_dict.pop('story_id')
                return jsonify(c_dict), 200
            except NotFoundError as e:
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), 404

        @comment_bp.route('/<int:id>', methods=['PUT'])
        def update_comment(id):
            try:
                data = request.get_json()
                if not data:
                    return jsonify({
                        "errorMessage": "Request body is required",
                        "errorCode": "40001"
                    }), 400

                # Преобразуем camelCase в snake_case
                converted_data = self._convert_request_data(data)
                result = self.comment_service.update(id, converted_data)

                c_dict = result.to_dict()
                if 'story_id' in c_dict:
                    c_dict['storyId'] = c_dict.pop('story_id')

                return jsonify(c_dict), 200

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

        @comment_bp.route('/<int:id>', methods=['DELETE'])
        def delete_comment(id):
            try:
                self.comment_service.delete(id)
                return Response(status=204)
            except NotFoundError as e:
                return jsonify({
                    "errorMessage": e.message,
                    "errorCode": e.error_code
                }), 404