from flask import Flask, jsonify, request, Response

app = Flask(__name__)

comments_storage = {}
next_id = 1


@app.route('/api/v1.0/comments', methods=['POST'])
def create_comment():
    global next_id
    data = request.get_json() or {}
    comment_id = next_id
    next_id += 1

    comment = {
        'id': comment_id,
        'storyId': data.get('storyId', data.get('story_id', 0)),
        'content': data.get('content', '')
    }
    comments_storage[comment_id] = comment
    return jsonify(comment), 201


@app.route('/api/v1.0/comments', methods=['GET'])
def get_comments():
    story_id = request.args.get('storyId', type=int)
    all_comments = list(comments_storage.values())
    if story_id:
        all_comments = [c for c in all_comments if c['storyId'] == story_id]
    return jsonify(all_comments), 200


@app.route('/api/v1.0/comments/<int:id>', methods=['GET'])
def get_comment(id):
    comment = comments_storage.get(id)
    if comment:
        return jsonify(comment), 200
    return jsonify({"errorMessage": f"Comment {id} not found", "errorCode": "40401"}), 404


@app.route('/api/v1.0/comments/<int:id>', methods=['PUT'])
def update_comment(id):
    if id not in comments_storage:
        return jsonify({"errorMessage": f"Comment {id} not found", "errorCode": "40401"}), 404
    data = request.get_json() or {}
    if 'content' in data:
        comments_storage[id]['content'] = data['content']
    return jsonify(comments_storage[id]), 200


@app.route('/api/v1.0/comments/<int:id>', methods=['DELETE'])
def delete_comment(id):
    if id in comments_storage:
        del comments_storage[id]
        return Response(status=204)
    return jsonify({"errorMessage": f"Comment {id} not found", "errorCode": "40401"}), 404


if __name__ == '__main__':
    print("Discussion service starting on http://localhost:24130")
    app.run(host='localhost', port=24130, debug=False)  # debug=False чтобы не было лишних таймаутов