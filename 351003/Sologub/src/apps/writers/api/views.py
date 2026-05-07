from rest_framework import viewsets, mixins
from apps.writers.api.serializers import WriterSerializer
from apps.writers.models import Writer


class WriterViewSet(viewsets.GenericViewSet,
                    mixins.ListModelMixin,
                    mixins.RetrieveModelMixin,
                    mixins.CreateModelMixin,
                    mixins.UpdateModelMixin,
                    mixins.DestroyModelMixin,):

    def get_queryset(self):
        return Writer.objects.all()

    def get_serializer_class(self):
        return WriterSerializer
