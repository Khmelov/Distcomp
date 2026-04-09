from tortoise import fields, models


class Post(models.Model):
    id = fields.IntField(pk=True)
    content = fields.TextField()
    issue = fields.ForeignKeyField("models.Issue", related_name="posts")
