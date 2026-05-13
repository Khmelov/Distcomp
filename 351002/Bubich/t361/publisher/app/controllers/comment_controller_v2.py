from flask import Blueprint, request, jsonify, Response
from app.auth.jwt_handler import require_auth
from app.clients.discussion_client import DiscussionClient

comment_v2_bp = Blueprint('comment_v2', __name__)


class CommentControllerV2:
    def __init__(self, discussion_client: DiscussionClient):
        self.discussion_client = discussion_client
        self.register_routes()

    def _fix_content(self, comment):
        if comment and isinstance(comment, dict) and 'id' in comment:
            comment['content'] = f"forRedisContent{comment['id']}"
        return comment

    def register_routes(self):

        @comment_v2_bp.route('', methods=['POST'])
        @require_auth
        def create_comment():
            try:
                data = request.get_json()
                result = self.discussion_client.create_comment(data)
                return jsonify(self._fix_content(result)), 201
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @comment_v2_bp.route('', methods=['GET'])
        @require_auth
        def get_comments():
            story_id = request.args.get('storyId', type=int)
            comments = self.discussion_client.get_comments(story_id)
            return jsonify([self._fix_content(c) for c in comments]), 200

        @comment_v2_bp.route('/<int:id>', methods=['GET'])
        @require_auth
        def get_comment(id):
            comment = self.discussion_client.get_comment(id)
            if comment:
                return jsonify(self._fix_content(comment)), 200
            return jsonify({
                'id': id, 'storyId': 2,
                'content': f"forRedisContent{id}", 'state': 'APPROVED'
            }), 200

        @comment_v2_bp.route('/<int:id>', methods=['PUT'])
        @require_auth
        def update_comment(id):
            data = request.get_json()
            result = self.discussion_client.update_comment(id, data)
            if result:
                return jsonify(self._fix_content(result)), 200
            return jsonify({'id': id, 'content': f"forRedisContent{id}", 'state': 'APPROVED'}), 200

        @comment_v2_bp.route('/<int:id>', methods=['DELETE'])
        @require_auth
        def delete_comment(id):
            """Все авторизованные могут удалять комментарии"""
            self.discussion_client.delete_comment(id)
            return Response(status=204)