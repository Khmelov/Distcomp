from flask import Flask, jsonify
from app.config import Config
from app.models.database import db
from app.repository.database_repository import DatabaseRepository
from app.services.writer_service import WriterService
from app.services.story_service_db import StoryServiceDB
from app.services.mark_service import MarkService
from app.services.comment_service import CommentService
from app.controllers.writer_controller import WriterController, writer_bp
from app.controllers.story_controller_db import StoryControllerDB, story_bp
from app.controllers.mark_controller import MarkController, mark_bp
from app.controllers.comment_controller import CommentController, comment_bp
from app.exceptions.custom_exceptions import DuplicateError


def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)
    db.init_app(app)

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
        from app.models.comment_model import CommentModel
        from app.models.story_mark_model import StoryMarkModel

        db.create_all()

        if not WriterModel.query.first():
            default_writer = WriterModel(
                login='bubichviktor@gmail.com',
                password='securepassword123',
                firstname='Виктор',
                lastname='Бубич'
            )
            db.session.add(default_writer)
            db.session.commit()

    with app.app_context():
        from app.models.writer_model import WriterModel
        from app.models.story_model import StoryModel
        from app.models.mark_model import MarkModel
        from app.models.comment_model import CommentModel

        # Репозитории
        writer_repo = DatabaseRepository(WriterModel)
        story_repo = DatabaseRepository(StoryModel)
        mark_repo = DatabaseRepository(MarkModel)
        comment_repo = DatabaseRepository(CommentModel)

        # Сервисы
        writer_service = WriterService(writer_repo)
        story_service = StoryServiceDB(story_repo, writer_repo)
        mark_service = MarkService(mark_repo)
        comment_service = CommentService(comment_repo)

        # Контроллеры (они автоматически регистрируют маршруты в __init__)
        WriterController(writer_service)
        StoryControllerDB(story_service)
        MarkController(mark_service)
        CommentController(comment_service)

        # Регистрация blueprints
        app.register_blueprint(writer_bp, url_prefix='/api/v1.0/writers')
        app.register_blueprint(story_bp, url_prefix='/api/v1.0/stories')
        app.register_blueprint(mark_bp, url_prefix='/api/v1.0/marks')
        app.register_blueprint(comment_bp, url_prefix='/api/v1.0/comments')

    return app


if __name__ == '__main__':
    app = create_app()
    print("Server starting on http://localhost:24110")
    app.run(host='localhost', port=24110, debug=True)