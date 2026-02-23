from django.core.validators import MinLengthValidator
from django.db import models
from apps.core.models import BaseModel


class Writer(BaseModel):
    login = models.CharField(max_length=64,
                             validators=[MinLengthValidator(2)],
                             unique=True)
    password = models.CharField(max_length=128,
                                validators=[MinLengthValidator(8)])
    firstname = models.CharField(max_length=64,
                                 validators=[MinLengthValidator(2)])
    lastname = models.CharField(max_length=64,
                                validators=[MinLengthValidator(2)])

    def __str__(self):
        return self.login
