from rest_framework import serializers, exceptions
from apps.writers.models import Writer


class WriterSerializer(serializers.ModelSerializer):

    class Meta:
        model = Writer
        fields = '__all__'

    def validate_login(self, value):
        if Writer.objects.filter(login=value).exclude(pk=getattr(self.instance, 'pk', None)).exists():
            raise exceptions.PermissionDenied("Writer with this login already exists")
        return value
