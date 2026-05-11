from tortoise import fields, models


class Editor(models.Model):
    id = fields.IntField(pk=True)
    login = fields.CharField(max_length=64, unique=True)
    password = fields.CharField(max_length=128)
    firstname = fields.CharField(max_length=64)
    lastname = fields.CharField(max_length=64)

    issues: fields.ReverseRelation["Issue"]

    class Meta:
        table = "tbl_editor"
