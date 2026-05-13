from app.models.writer_model import WriterModel
from app.auth.jwt_handler import JWTHandler
from app.auth.password_utils import hash_password, verify_password
from app.exceptions.custom_exceptions import ValidationError, NotFoundError


class AuthService:
    """Сервис аутентификации"""

    def register(self, data: dict) -> dict:
        """Регистрация нового пользователя"""
        login = data.get('login')
        password = data.get('password')
        firstname = data.get('firstname', data.get('firstName', ''))
        lastname = data.get('lastname', data.get('lastName', ''))
        role = data.get('role', 'CUSTOMER')

        # Валидация
        if not login or len(login) < 2 or len(login) > 64:
            raise ValidationError("Login must be between 2 and 64 characters", "40002")
        if not password or len(password) < 8 or len(password) > 128:
            raise ValidationError("Password must be between 8 and 128 characters", "40003")
        if role not in ['ADMIN', 'CUSTOMER']:
            raise ValidationError("Role must be ADMIN or CUSTOMER", "40004")

        # Проверка уникальности логина
        existing = WriterModel.query.filter_by(login=login).first()
        if existing:
            raise ValidationError(f"Login '{login}' already exists", "40301")

        # Создание пользователя
        writer = WriterModel(
            login=login,
            password=hash_password(password),
            firstname=firstname,
            lastname=lastname
        )

        from app.models.database import db
        db.session.add(writer)
        db.session.commit()

        return {
            'id': writer.id,
            'login': writer.login,
            'firstname': writer.firstname,
            'lastname': writer.lastname
        }

    def login(self, login: str, password: str) -> dict:
        """Аутентификация пользователя"""
        writer = WriterModel.query.filter_by(login=login).first()
        if not writer:
            raise ValidationError("Invalid login or password", "40101")

        if not verify_password(password, writer.password):
            raise ValidationError("Invalid login or password", "40101")

        # Определяем роль (по умолчанию CUSTOMER, ADMIN если login содержит 'admin')
        role = 'ADMIN' if 'admin' in login.lower() else 'CUSTOMER'

        # Генерируем токен
        token = JWTHandler.generate_token(login, role)

        return {
            'access_token': token,
            'token_type': 'Bearer',
            'login': login,
            'role': role
        }