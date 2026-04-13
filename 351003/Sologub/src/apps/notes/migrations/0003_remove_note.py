# Migration: перенос хранения Note в микросервис discussion (Cassandra).
# Таблица tbl_note в Postgres больше не используется.

from django.db import migrations


class Migration(migrations.Migration):

    dependencies = [
        ("notes", "0002_alter_note_table"),
    ]

    operations = [
        migrations.DeleteModel(name="Note"),
    ]
