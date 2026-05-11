from flask import Flask, jsonify, request, Response

app = Flask(__name__)
comments = {}
next_id = 1


@app.route('/api/v1.0/comments', methods=['POST'])
def create():
    global next_id
    data = request.get_json() or {}
    comment_id = next_id
    next_id += 1

    c = {
        'id': comment_id,
        'storyId': data.get('storyId', 0),
        'content': data.get('content', ''),  # ← Сохраняем как есть!
        'state': 'APPROVED'
    }
    comments[comment_id] = c
    return jsonify(c), 201


@app.route('/api/v1.0/comments', methods=['GET'])
def get_all():
    story_id = request.args.get('storyId', type=int)
    all_c = list(comments.values())
    if story_id:
        all_c = [c for c in all_c if c['storyId'] == story_id]
    return jsonify(all_c), 200


@app.route('/api/v1.0/comments/<int:id>', methods=['GET'])
def get_one(id):
    c = comments.get(id)
    if c:
        return jsonify(c), 200  # ← Возвращаем как есть!
    return jsonify({"errorMessage": "Not found", "errorCode": "40401"}), 404


@app.route('/api/v1.0/comments/<int:id>', methods=['PUT'])
def update(id):
    data = request.get_json() or {}
    if id not in comments:
        comments[id] = {'id': id, 'storyId': data.get('storyId', 0), 'state': 'APPROVED'}
    if 'content' in data:
        comments[id]['content'] = data['content']  # ← Сохраняем как есть!
    if 'storyId' in data:
        comments[id]['storyId'] = data['storyId']
    return jsonify(comments[id]), 200


@app.route('/api/v1.0/comments/<int:id>', methods=['DELETE'])
def delete(id):
    if id in comments:
        del comments[id]
    return Response(status=204)


if __name__ == '__main__':
    print("Discussion on http://localhost:24130")
    app.run(host='localhost', port=24130, debug=False)