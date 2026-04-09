TORTOISE_CONFIG = {
    "connections": {
        "default": {
            "engine": "tortoise.backends.sqlite",
            "credentials": {"file_path": "db.sqlite3"},
        }
    },
    "apps": {
        "models": {
            "models": [
                "src.models.editor",
                "src.models.post",
                "src.models.issue",
                "src.models.label",
            ],
            "default_connection": "default",
        }
    },
}
