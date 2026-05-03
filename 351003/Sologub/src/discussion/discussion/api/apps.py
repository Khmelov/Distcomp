import os
from django.apps import AppConfig


class ApiConfig(AppConfig):
    default_auto_field = "django.db.models.BigAutoField"
    name = "discussion.api"
    label = "discussion_api"

    def ready(self):
        if os.environ.get("RUN_MAIN") == "true":
            from kafka_handler import start_consumer
            start_consumer()
