from django.core.validators import MinLengthValidator
from django.db import models
from apps.core.models import BaseModel
from apps.markers.models import Marker
from apps.writers.models import Writer


class Story(BaseModel):
    writerId = models.ForeignKey(Writer, on_delete=models.RESTRICT)
    title = models.CharField(max_length=64,
                             validators=[MinLengthValidator(2)],
                             unique=True)
    content = models.TextField(max_length=2048, validators=[MinLengthValidator(4)])
    created_at = models.DateTimeField(auto_now_add=True)
    modified_at = models.DateTimeField(auto_now_add=True)
    markers = models.ManyToManyField(Marker, blank=True)

    def __str__(self):
        return self.title