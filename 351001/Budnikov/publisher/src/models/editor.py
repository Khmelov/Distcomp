from tortoise import fields, models


class Editor(models.Model):
    id = fields.IntField(pk=True)
    login = fields.CharField(max_length=64, unique=True)
    password = fields.CharField(max_length=128)
    firstname = fields.CharField(max_length=64)
    lastname = fields.CharField(max_length=64)
    role = fields.CharField(max_length=32, default="CUSTOMER")

    issues: fields.ReverseRelation["Issue"]

    class Meta:
        table = "tbl_editor"