import sys
import os

sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from flask import Flask, jsonify
from app.config import Config
from app.models.database import db
from app.cache.redis_cache import RedisCache
from app.repository.database_repository import DatabaseRepository
from app.services.writer_service import WriterService
from app.services.story_service import StoryService
from app.services.mark_service import MarkService
from app.clients.discussion_client import DiscussionClient
from app.controllers.writer_controller import WriterController, writer_bp
from app.controllers.story_controller import StoryController, story_bp
from app.controllers.mark_controller import MarkController, mark_bp
from app.controllers.comment_controller import CommentController, comment_bp


def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)
    db.init_app(app)

    # Инициализация Redis (один раз!)
    redis_cache = RedisCache()

    @app.errorhandler(400)
    def bad_request(error):
        return jsonify({"errorMessage": "Bad request", "errorCode": "40000"}), 400

    @app.errorhandler(403)
    def forbidden(error):
        return jsonify({"errorMessage": "Forbidden", "errorCode": "40300"}), 403

    @app.errorhandler(404)
    def not_found(error):
        return jsonify({"errorMessage": "Resource not found", "errorCode": "40400"}), 404

    @app.errorhandler(500)
    def internal_error(error):
        return jsonify({"errorMessage": "Internal server error", "errorCode": "50000"}), 500

    with app.app_context():
        from app.models.writer_model import WriterModel
        from app.models.story_model import StoryModel
        from app.models.mark_model import MarkModel

        db.create_all()

        if not WriterModel.query.first():
            default_writer = WriterModel(
                login='bubichviktorgmail.com',
                password='securepassword123',
                firstname='Bekirop',
                lastname='Бубен'  # ← Обновлено по заданию!
            )
            db.session.add(default_writer)
            db.session.commit()
            print("Default writer created: Бубен")

    with app.app_context():
        from app.models.writer_model import WriterModel
        from app.models.story_model import StoryModel
        from app.models.mark_model import MarkModel

        # Репозитории
        writer_repo = DatabaseRepository(WriterModel)
        story_repo = DatabaseRepository(StoryModel)
        mark_repo = DatabaseRepository(MarkModel)

        # Discussion клиент с Redis
        discussion_client = DiscussionClient(redis_cache)

        # Сервисы с Redis
        writer_service = WriterService(writer_repo, redis_cache)
        story_service = StoryService(story_repo, writer_repo, redis_cache)
        mark_service = MarkService(mark_repo, redis_cache)

        # Контроллеры (каждый создаётся ОДИН раз)
        WriterController(writer_service)
        StoryController(story_service)
        MarkController(mark_service)
        CommentController(discussion_client)

        # Регистрация blueprints
        app.register_blueprint(writer_bp, url_prefix='/api/v1.0/writers')
        app.register_blueprint(story_bp, url_prefix='/api/v1.0/stories')
        app.register_blueprint(mark_bp, url_prefix='/api/v1.0/marks')
        app.register_blueprint(comment_bp, url_prefix='/api/v1.0/comments')

    return app


if __name__ == '__main__':
    app = create_app()
    print("Publisher with Redis starting on http://localhost:24110")
    app.run(host='localhost', port=24110, debug=True)