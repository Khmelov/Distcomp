import os


def default_country() -> str:
    return os.getenv("MESSAGE_DEFAULT_COUNTRY", "BY")
