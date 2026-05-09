from django.core.validators import MinLengthValidator
from django.db import models


class Editor(models.Model):
    ROLE_CHOICES = [
        ('ADMIN', 'Admin'),
        ('CUSTOMER', 'Customer'),
    ]

    class Meta:
        db_table = 'tbl_editor'

    id = models.BigAutoField(
        primary_key=True,
        unique=True
    )
    login = models.CharField(
        max_length=64,
        unique=True,
        validators=[MinLengthValidator(2)]
    )
    password = models.CharField(
        max_length=128,
        validators=[MinLengthValidator(8)]
    )
    firstname = models.CharField(
        max_length=64,
        validators=[MinLengthValidator(2)]
    )
    lastname = models.CharField(
        max_length=64,
        validators=[MinLengthValidator(2)]
    )
    role = models.CharField(
        max_length=10,
        choices=ROLE_CHOICES,
        default='CUSTOMER'
    )

    def __str__(self):
        return f'{self.login} | {self.firstname} | {self.lastname}'

class Label(models.Model):
    class Meta:
        db_table = 'tbl_label'
    id = models.BigAutoField(
        primary_key=True,
        unique=True
    )
    name = models.CharField(
        max_length=32,
        validators=[MinLengthValidator(2)]
    )

    def __str__(self):
        return f'{self.name}'

class Issue(models.Model):
    class Meta:
        db_table = 'tbl_issue'
    id = models.BigAutoField(
        primary_key=True,
        unique=True
    )
    editor = models.ForeignKey(
        Editor,
        on_delete=models.CASCADE,
        related_name='issues',
    )
    title = models.CharField(
        max_length=64,
        validators=[MinLengthValidator(2)]
    )
    content = models.TextField(
        max_length=2048,
        validators=[MinLengthValidator(4)]
    )
    created = models.DateTimeField(auto_now_add=True)
    modified = models.DateTimeField(auto_now=True)
    labels = models.ManyToManyField(Label, related_name='issues')

    def __str__(self):
        return f'{self.editor} | {self.title} | {self.content}'

class Message(models.Model):
    class Meta:
        db_table = 'tbl_message'
    id = models.BigAutoField(
        primary_key=True,
        unique=True
    )
    issue = models.ForeignKey(
        Issue,
        on_delete=models.CASCADE,
        related_name='messages',
    )
    content = models.TextField(
        max_length=2048,
        validators=[MinLengthValidator(2)]
    )

    def __str__(self):
        return f'{self.issue} | {self.content}'
