import sqlite3
import os

# Правильный путь к базе данных
db_path = r'C:\Users\User\Desktop\3c\RV\t320\instance\distcomp.db'

print(f"Путь к базе: {db_path}")

if os.path.exists(db_path):
    print("База данных найдена. Очищаем...")

    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    try:
        # Удаляем данные с учетом внешних ключей
        cursor.execute("PRAGMA foreign_keys = ON")
        cursor.execute("DELETE FROM tbl_comment")
        cursor.execute("DELETE FROM tbl_story_mark")
        cursor.execute("DELETE FROM tbl_story")
        cursor.execute("DELETE FROM tbl_mark")
        cursor.execute("DELETE FROM tbl_writer")
        conn.commit()
        print("✓ Все данные удалены!")

        # Создаем начального writer
        cursor.execute("""
            INSERT INTO tbl_writer (login, password, firstname, lastname) 
            VALUES ('bubichviktor@gmail.com', 'securepassword123', 'Виктор', 'Бубич')
        """)
        conn.commit()
        print("✓ Writer создан: bubichviktor@gmail.com")

        # Проверка
        cursor.execute("SELECT * FROM tbl_writer")
        writers = cursor.fetchall()
        print(f"\nТекущие writers в БД:")
        for w in writers:
            print(f"  ID={w[0]}, Login={w[1]}")

    except Exception as e:
        print(f"Ошибка: {e}")
        conn.rollback()
    finally:
        conn.close()
else:
    print(f"❌ База данных не найдена: {db_path}")