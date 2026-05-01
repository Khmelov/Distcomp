import os
from django.apps import AppConfig


class NotesConfig(AppConfig):
    name = 'apps.notes'

    def ready(self):
        if os.environ.get("RUN_MAIN") == "true":
            from apps.notes.kafka_consumer import start_consumer
            start_consumer()
