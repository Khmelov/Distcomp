from flask import Blueprint, request, jsonify, Response

comment_bp = Blueprint('comment', __name__)


class CommentController:
    def __init__(self, discussion_client):
        self.discussion_client = discussion_client
        self.register_routes()

    def register_routes(self):
        @comment_bp.route('', methods=['POST'])
        def create_comment():
            try:
                data = request.get_json()
                if not data:
                    return jsonify({"errorMessage": "Request body is required", "errorCode": "40001"}), 400
                result = self.discussion_client.create_comment(data)
                return jsonify(result), 201
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @comment_bp.route('', methods=['GET'])
        def get_comments():
            try:
                story_id = request.args.get('storyId', type=int)
                comments = self.discussion_client.get_comments(story_id)
                return jsonify(comments), 200
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @comment_bp.route('/<int:id>', methods=['GET'])
        def get_comment(id):
            try:
                comment = self.discussion_client.get_comment(id)
                if comment:
                    return jsonify(comment), 200
                return jsonify({"errorMessage": f"Comment {id} not found", "errorCode": "40401"}), 404
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @comment_bp.route('/<int:id>', methods=['PUT'])
        def update_comment(id):
            try:
                data = request.get_json()
                result = self.discussion_client.update_comment(id, data)
                if result:
                    return jsonify(result), 200
                return jsonify({"errorMessage": f"Comment {id} not found", "errorCode": "40401"}), 404
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @comment_bp.route('/<int:id>', methods=['DELETE'])
        def delete_comment(id):
            try:
                success = self.discussion_client.delete_comment(id)
                if success:
                    return Response(status=204)
                return jsonify({"errorMessage": f"Comment {id} not found", "errorCode": "40401"}), 404
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400