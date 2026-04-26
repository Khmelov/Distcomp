from django.core.validators import MinLengthValidator
from django.db import models
from apps.core.models import BaseModel


class Writer(BaseModel):
    class Meta:
        db_table = 'tbl_writer'

    ROLE_ADMIN = 'ADMIN'
    ROLE_CUSTOMER = 'CUSTOMER'
    ROLE_CHOICES = (
        (ROLE_ADMIN, 'Admin'),
        (ROLE_CUSTOMER, 'Customer'),
    )

    login = models.CharField(max_length=64,
                             validators=[MinLengthValidator(2)])
    password = models.CharField(max_length=128,
                                validators=[MinLengthValidator(8)])
    firstname = models.CharField(max_length=64,
                                 validators=[MinLengthValidator(2)])
    lastname = models.CharField(max_length=64,
                                validators=[MinLengthValidator(2)])
    role = models.CharField(max_length=16,
                            choices=ROLE_CHOICES,
                            default=ROLE_CUSTOMER)

    def __str__(self):
        return self.login
