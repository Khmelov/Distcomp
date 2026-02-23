from rest_framework import serializers
from apps.stories.models import Story


class StorySerializer(serializers.ModelSerializer):

    class Meta:
        model = Story
        fields = '__all__'
