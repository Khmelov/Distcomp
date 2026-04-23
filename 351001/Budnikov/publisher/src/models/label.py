from tortoise import fields, models


class Label(models.Model):
    id = fields.IntField(pk=True)
    name = fields.CharField(max_length=32, unique=True)

    issues: fields.ManyToManyRelation["Issue"]

    class Meta:
        table = "tbl_label"
