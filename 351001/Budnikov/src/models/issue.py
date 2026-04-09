from tortoise import fields, models


class Issue(models.Model):
    id = fields.BigIntField(pk=True)
    editor = fields.ForeignKeyField("models.Editor", related_name="issues")
    title = fields.CharField(max_length=64)
    content = fields.CharField(max_length=2048)
    created = fields.DatetimeField(auto_now_add=True)
    modified = fields.DatetimeField(auto_now=True)
    labels = fields.ManyToManyField("models.Label", related_name="issues", through="m2m_issues_labels")

    posts: fields.ReverseRelation["Post"]

    class Meta:
        table = "issues"
