from django.core.validators import MinLengthValidator
from django.db import models
from apps.core.models import BaseModel
from apps.markers.models import Marker
from apps.writers.models import Writer


class Story(BaseModel):
    class Meta:
        db_table = 'tbl_story'

    writerId = models.ForeignKey(Writer, on_delete=models.RESTRICT,
                                 db_column='writer_id')
    title = models.CharField(max_length=64,
                             validators=[MinLengthValidator(2)])
    content = models.TextField(max_length=2048, validators=[MinLengthValidator(4)])
    created_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now_add=True)
    markers = models.ManyToManyField(Marker, blank=True)

    def __str__(self):
        return self.title