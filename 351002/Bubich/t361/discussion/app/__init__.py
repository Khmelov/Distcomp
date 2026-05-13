from flask import Flask
from flask_cors import CORS
from app.config import Config
from app.repository.cassandra_repository import CassandraRepository
from app.services.comment_service import CommentService
from app.controllers.comment_controller import CommentController, comment_bp


def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)
    CORS(app)

    # Инициализация Cassandra
    cassandra_repo = CassandraRepository()
    cassandra_repo.init_schema()

    # Сервис
    comment_service = CommentService(cassandra_repo)

    # Контроллер
    comment_controller = CommentController(comment_service)

    # Регистрация blueprint
    app.register_blueprint(comment_bp, url_prefix='/api/v1.0')

    return app