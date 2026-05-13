from flask import Blueprint, request, jsonify, Response
from app.services.comment_service import CommentService
from werkzeug.exceptions import NotFound, BadRequest

comment_bp = Blueprint('comment', __name__)


class CommentController:
    def __init__(self, comment_service: CommentService):
        self.comment_service = comment_service
        self.register_routes()

    def register_routes(self):
        @comment_bp.route('/comments', methods=['POST'])
        def create_comment():
            try:
                data = request.get_json()
                if not data:
                    return jsonify({
                        "errorMessage": "Request body is required",
                        "errorCode": "40001"
                    }), 400

                # Поддержка camelCase и snake_case
                if 'storyId' in data:
                    data['story_id'] = data['storyId']

                result = self.comment_service.create(data)
                return jsonify({
                    'id': result['id'],
                    'storyId': result['story_id'],
                    'content': result['content']
                }), 201
            except BadRequest as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40000"
                }), 400
            except Exception as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40000"
                }), 400

        @comment_bp.route('/comments', methods=['GET'])
        def get_comments():
            try:
                story_id = request.args.get('storyId', type=int)
                if story_id:
                    comments = self.comment_service.get_by_story_id(story_id)
                else:
                    comments = self.comment_service.get_all()

                result = []
                for c in comments:
                    result.append({
                        'id': c['id'],
                        'storyId': c['story_id'] if 'story_id' in c else c.get('storyId'),
                        'content': c['content']
                    })

                return jsonify(result), 200
            except Exception as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40000"
                }), 400

        @comment_bp.route('/comments/<int:id>', methods=['GET'])
        def get_comment(id):
            try:
                comment = self.comment_service.get_by_id(id)
                return jsonify({
                    'id': comment['id'],
                    'storyId': comment['story_id'] if 'story_id' in comment else comment.get('storyId'),
                    'content': comment['content']
                }), 200
            except NotFound as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40401"
                }), 404

        @comment_bp.route('/comments/<int:id>', methods=['PUT'])
        def update_comment(id):
            try:
                data = request.get_json()
                if 'storyId' in data:
                    data['story_id'] = data['storyId']

                result = self.comment_service.update(id, data)
                return jsonify({
                    'id': result['id'],
                    'storyId': result['story_id'],
                    'content': result['content']
                }), 200
            except NotFound as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40401"
                }), 404
            except BadRequest as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40000"
                }), 400

        @comment_bp.route('/comments/<int:id>', methods=['DELETE'])
        def delete_comment(id):
            try:
                self.comment_service.delete(id)
                return Response(status=204)
            except NotFound as e:
                return jsonify({
                    "errorMessage": str(e),
                    "errorCode": "40401"
                }), 404