from rest_framework import serializers, exceptions
from apps.stories.models import Story
from apps.markers.models import Marker


class CreatableSlugRelatedField(serializers.SlugRelatedField):
    def to_internal_value(self, data):
        try:
            obj, created = self.get_queryset().get_or_create(**{self.slug_field: data})
            return obj
        except (TypeError, ValueError):
            self.fail('invalid')


class StorySerializer(serializers.ModelSerializer):
    markers = CreatableSlugRelatedField(
        many=True,
        slug_field='name',
        queryset=Marker.objects.all(),
        required=False
    )

    class Meta:
        model = Story
        fields = '__all__'

    def validate_title(self, value):
        if Story.objects.filter(title=value).exclude(pk=getattr(self.instance, 'pk', None)).exists():
            raise exceptions.PermissionDenied("Story with this title already exists")
        return value
