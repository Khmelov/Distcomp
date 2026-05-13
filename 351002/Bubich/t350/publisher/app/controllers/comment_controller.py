from flask import Blueprint, request, jsonify, Response

comment_bp = Blueprint('comment_publisher', __name__)

class CommentController:
    _instance = None

    def __new__(cls, *args, **kwargs):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
        return cls._instance

    def __init__(self, discussion_client):
        if hasattr(self, '_initialized') and self._initialized:
            return
        self.discussion_client = discussion_client
        self._initialized = True
        self.register_routes()

    def register_routes(self):
        @comment_bp.route('', methods=['POST'])
        def create_comment():
            try:
                data = request.get_json()
                result = self.discussion_client.create_comment(data)
                return jsonify(result), 201
            except Exception as e:
                return jsonify({"errorMessage": str(e), "errorCode": "40000"}), 400

        @comment_bp.route('', methods=['GET'])
        def get_comments():
            story_id = request.args.get('storyId', type=int)
            comments = self.discussion_client.get_comments(story_id)
            return jsonify(comments or []), 200

        @comment_bp.route('/<int:id>', methods=['GET'])
        def get_comment(id):
            comment = self.discussion_client.get_comment(id)
            if comment:
                return jsonify(comment), 200
            return jsonify({"errorMessage": "Not found", "errorCode": "40401"}), 404

        @comment_bp.route('/<int:id>', methods=['PUT'])
        def update_comment(id):
            data = request.get_json()
            result = self.discussion_client.update_comment(id, data)
            if result:
                return jsonify(result), 200
            return jsonify({"errorMessage": "Not found", "errorCode": "40401"}), 404

        @comment_bp.route('/<int:id>', methods=['DELETE'])
        def delete_comment(id):
            self.discussion_client.delete_comment(id)
            return Response(status=204)