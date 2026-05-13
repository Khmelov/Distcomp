from flask import Blueprint, request, jsonify, Response
from werkzeug.exceptions import NotFound, BadRequest

comment_bp = Blueprint('comment', __name__)


class CommentController:
    def __init__(self, comment_service, kafka_handler=None):
        self.service = comment_service
        self.kafka = kafka_handler
        self.register_routes()

    def register_routes(self):
        @comment_bp.route('/comments', methods=['POST'])
        def create_comment():
            try:
                data = request.get_json() or {}
                result = self.service.create(data)
                return jsonify({
                    'id': result['id'],
                    'storyId': result['story_id'],
                    'content': result['content'],
                    'state': result.get('state', 'APPROVED')
                }), 201
            except BadRequest as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @comment_bp.route('/comments', methods=['GET'])
        def get_comments():
            story_id = request.args.get('storyId', type=int)
            if story_id:
                comments = self.service.get_by_story_id(story_id)
            else:
                comments = self.service.get_all()
            result = [{'id': c['id'], 'storyId': c.get('story_id', 0), 'content': c['content'],
                       'state': c.get('state', 'APPROVED')} for c in comments]
            return jsonify(result), 200

        @comment_bp.route('/comments/<int:id>', methods=['GET'])
        def get_comment(id):
            try:
                c = self.service.get_by_id(id)
                return jsonify({'id': c['id'], 'storyId': c.get('story_id', 0), 'content': c['content'],
                                'state': c.get('state', 'APPROVED')}), 200
            except NotFound:
                return jsonify({"errorMessage": "Not found", "errorCode": "40401"}), 404

        @comment_bp.route('/comments/<int:id>', methods=['PUT'])
        def update_comment(id):
            try:
                data = request.get_json() or {}
                c = self.service.update(id, data)
                return jsonify({'id': c['id'], 'storyId': c.get('story_id', 0), 'content': c['content'],
                                'state': c.get('state', 'APPROVED')}), 200
            except NotFound:
                return jsonify({"errorMessage": "Not found", "errorCode": "40401"}), 404

        @comment_bp.route('/comments/<int:id>', methods=['DELETE'])
        def delete_comment(id):
            try:
                self.service.delete(id)
                return Response(status=204)
            except NotFound:
                return jsonify({"errorMessage": "Not found", "errorCode": "40401"}), 404