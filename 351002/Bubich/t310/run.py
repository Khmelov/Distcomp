from flask import Flask, jsonify
from app.repository.in_memory_repository import InMemoryRepository
from app.services.writer_service import WriterService
from app.services.story_service import StoryService
from app.services.mark_service import MarkService
from app.services.comment_service import CommentService
from app.controllers.writer_controller import WriterController, writer_bp
from app.controllers.story_controller import StoryController, story_bp
from app.controllers.mark_controller import MarkController, mark_bp
from app.controllers.comment_controller import CommentController, comment_bp


def create_app():
    app = Flask(__name__)

    # Репозитории
    writer_repo = InMemoryRepository()
    story_repo = InMemoryRepository()
    mark_repo = InMemoryRepository()
    comment_repo = InMemoryRepository()

    # Сервисы
    writer_service = WriterService(writer_repo)
    story_service = StoryService(story_repo)
    mark_service = MarkService(mark_repo)
    comment_service = CommentService(comment_repo)

    # Контроллеры
    writer_controller = WriterController(writer_service)
    story_controller = StoryController(story_service)
    mark_controller = MarkController(mark_service)
    comment_controller = CommentController(comment_service)

    # Регистрация blueprints - ИСПОЛЬЗУЕМ "stories" вместо "storys"
    app.register_blueprint(writer_controller.register_routes(), url_prefix='/api/v1.0/writers')
    app.register_blueprint(story_controller.register_routes(), url_prefix='/api/v1.0/stories')  # ← изменено
    app.register_blueprint(mark_controller.register_routes(), url_prefix='/api/v1.0/marks')
    app.register_blueprint(comment_controller.register_routes(), url_prefix='/api/v1.0/comments')

    # Обработчики ошибок
    @app.errorhandler(400)
    def bad_request(error):
        return jsonify({
            "errorMessage": "Bad request",
            "errorCode": "40000"
        }), 400

    @app.errorhandler(404)
    def not_found(error):
        return jsonify({
            "errorMessage": "Resource not found",
            "errorCode": "40400"
        }), 404

    @app.errorhandler(405)
    def method_not_allowed(error):
        return jsonify({
            "errorMessage": "Method not allowed",
            "errorCode": "40500"
        }), 405

    @app.errorhandler(500)
    def internal_error(error):
        return jsonify({
            "errorMessage": "Internal server error",
            "errorCode": "50000"
        }), 500

    return app


if __name__ == '__main__':
    app = create_app()
    app.run(host='localhost', port=24110, debug=True)