from django.core.validators import MinLengthValidator
from django.db import models
from apps.core.models import BaseModel
from apps.stories.models import Story


class Note(BaseModel):
    class Meta:
        db_table = 'tbl_note'

    storyId = models.ForeignKey(Story, on_delete=models.RESTRICT)
    content = models.TextField(max_length=2048, validators=[MinLengthValidator(2)])

    def __str__(self):
        return self.id
