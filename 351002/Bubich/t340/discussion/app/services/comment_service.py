class CommentService:
    STOP_WORDS = ['spam', 'badword', 'xxx']

    def __init__(self, repository):
        self.repository = repository

    def create(self, data):
        content = data.get('content', '')
        comment = {
            'id': data.get('id', 0),
            'story_id': data.get('storyId', data.get('story_id', 0)),
            'content': content,
            'state': 'DECLINED' if any(w in content.lower() for w in self.STOP_WORDS) else 'APPROVED'
        }
        return self.repository.save(comment)

    def get_by_id(self, id):
        c = self.repository.find_by_id(id)
        if not c:
            from werkzeug.exceptions import NotFound
            raise NotFound(f"Comment {id} not found")
        return c

    def get_by_story_id(self, story_id):
        return self.repository.find_by_story_id(story_id)

    def get_all(self):
        return self.repository.find_all()

    def update(self, id, data):
        return self.repository.update(id, data)

    def delete(self, id):
        self.repository.delete_by_id(id)