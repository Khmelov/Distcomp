from rest_framework import serializers
from .models import Comment

class CommentSerializer(serializers.Serializer):
    id = serializers.IntegerField()
    tweetId = serializers.IntegerField()
    country = serializers.CharField()
    content = serializers.CharField()

    def create(self, validated_data):
        return Comment.objects.create(**validated_data)

    def update(self, instance, validated_data):
        instance.content = validated_data.get('content', instance.content)
        instance.save()
        return instance