from rest_framework import mixins, viewsets
from apps.stories.api.serializers import StorySerializer
from apps.stories.models import Story


class StoryViewSet(viewsets.GenericViewSet,
                    mixins.CreateModelMixin,
                    mixins.ListModelMixin,
                    mixins.RetrieveModelMixin,
                    mixins.UpdateModelMixin,
                    mixins.DestroyModelMixin):

    def get_queryset(self):
        return Story.objects.all()

    def get_serializer_class(self):
        return StorySerializer

    def perform_destroy(self, instance):
        markers = list(instance.markers.all())
        instance.delete()
        for marker in markers:
            if marker.story_set.count() == 0:
                marker.delete()
