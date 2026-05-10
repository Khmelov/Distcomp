import logging

# Создаём логгер
logger = logging.getLogger("request_logger")
logger.setLevel(logging.DEBUG)  # DEBUG — самый подробный уровень

# Создаём handler (вывод в консоль)
ch = logging.StreamHandler()
ch.setLevel(logging.DEBUG)

# Формат вывода
formatter = logging.Formatter(
    '%(asctime)s - %(levelname)s - %(message)s'
)
ch.setFormatter(formatter)
logger.addHandler(ch)
