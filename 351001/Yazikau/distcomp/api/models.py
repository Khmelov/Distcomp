from django.db import models
from django.core.validators import MinLengthValidator
from django.db.models.signals import post_delete
from django.dispatch import receiver

# --- ИМПОРТЫ КАССАНДРЫ ---
from cassandra.cqlengine import columns
from cassandra.cqlengine.models import Model as CassandraModel


class Author(models.Model):
    login = models.CharField(max_length=64, validators=[MinLengthValidator(2)], unique=True)
    # Вот он, спаситель тестов — MinLengthValidator(8) снова в деле!
    password = models.CharField(max_length=128, validators=[MinLengthValidator(8)])
    firstname = models.CharField(max_length=64, validators=[MinLengthValidator(2)])
    lastname = models.CharField(max_length=64, validators=[MinLengthValidator(2)])

    role = models.CharField(max_length=10, default='customer')

    class Meta:
        db_table = 'tbl_author'

    def __str__(self):
        return self.login


class Mark(models.Model):
    name = models.CharField(max_length=32, validators=[MinLengthValidator(2)])

    class Meta:
        db_table = 'tbl_mark'


class Tweet(models.Model):
    author = models.ForeignKey(Author, on_delete=models.CASCADE, related_name='tweets')
    title = models.CharField(max_length=64, validators=[MinLengthValidator(2)])
    content = models.TextField(max_length=2048, validators=[MinLengthValidator(4)])
    created = models.DateTimeField(auto_now_add=True)
    modified = models.DateTimeField(auto_now=True)
    marks = models.ManyToManyField(Mark, related_name='tweets', blank=True, db_table='tbl_tweet_marks')

    class Meta:
        db_table = 'tbl_tweet'


@receiver(post_delete, sender=Tweet)
def cleanup_marks(sender, instance, **kwargs):
    Mark.objects.filter(tweets__isnull=True).delete()


# --- МОДЕЛЬ КАССАНДРЫ ---
class CassandraComment(CassandraModel):
    __keyspace__ = 'distcomp'  # Наш фикс для 19-го теста
    id = columns.BigInt(primary_key=True)
    tweetId = columns.BigInt(index=True)
    content = columns.Text()
    country = columns.Text(default='Default')