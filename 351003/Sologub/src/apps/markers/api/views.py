from rest_framework import mixins, viewsets
from apps.markers.api.serializers import MarkerSerializer
from apps.markers.models import Marker


class MarkerViewSet(viewsets.GenericViewSet,
                    mixins.CreateModelMixin,
                    mixins.ListModelMixin,
                    mixins.RetrieveModelMixin,
                    mixins.UpdateModelMixin,
                    mixins.DestroyModelMixin):

    def get_queryset(self):
        return Marker.objects.all()

    def get_serializer_class(self):
        return MarkerSerializer
