# logger.py
import logging
import sys


def setup_logging():
    # Формат логов: время - уровень - сообщение
    log_format = "%(asctime)s - %(levelname)s - %(message)s"
    date_format = "%Y-%m-%d %H:%M:%S"

    # Настройка корневого логгера
    logging.basicConfig(
        level=logging.INFO,
        format=log_format,
        datefmt=date_format,
        handlers=[
            logging.StreamHandler(sys.stdout),  # вывод в консоль
            logging.FileHandler("app.log", encoding="utf-8"),  # запись в файл
        ],
    )

    # Можно также получить отдельный логгер для приложения
    logger = logging.getLogger("app")
    return logger


app_logger = setup_logging()
