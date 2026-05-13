from app.config import Config
from sqlalchemy import create_engine, text


def test_postgres_connection():
    try:
        engine = create_engine(Config.SQLALCHEMY_DATABASE_URI)
        with engine.connect() as conn:
            result = conn.execute(text("SELECT 1"))
            print("✓ PostgreSQL подключен успешно!")

            # Проверка таблиц
            result = conn.execute(text("""
                SELECT table_name 
                FROM information_schema.tables 
                WHERE table_schema = 'distcomp'
            """))
            tables = result.fetchall()
            print(f"Таблицы: {[t[0] for t in tables]}")

        return True
    except Exception as e:
        print(f"✗ Ошибка подключения: {e}")
        return False


if __name__ == "__main__":
    test_postgres_connection()