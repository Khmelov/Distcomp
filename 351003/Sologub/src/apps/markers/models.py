from django.core.validators import MinLengthValidator
from django.db import models
from apps.core.models import BaseModel


class Marker(BaseModel):
    class Meta:
        db_table = 'tbl_marker'

    name = models.CharField(max_length=32,
                            unique=True,
                            validators=[MinLengthValidator(2)])

    def __str__(self):
        return self.name
