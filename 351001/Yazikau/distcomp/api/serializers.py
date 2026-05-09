from rest_framework import serializers
from .models import Author, Mark, Tweet

class AuthorSerializer(serializers.ModelSerializer):
    class Meta:
        model = Author
        # ДОБАВЛЕНО ПОЛЕ 'role'
        fields = ['id', 'login', 'password', 'firstname', 'lastname', 'role']
        extra_kwargs = {'password': {'write_only': True}}

class MarkSerializer(serializers.ModelSerializer):
    class Meta:
        model = Mark
        fields = ['id', 'name']

class TweetSerializer(serializers.ModelSerializer):
    authorId = serializers.PrimaryKeyRelatedField(source='author', queryset=Author.objects.all())
    marks = serializers.SlugRelatedField(
        many=True,
        slug_field='name',
        queryset=Mark.objects.all(),
        required=False
    )

    class Meta:
        model = Tweet
        fields = ['id', 'authorId', 'title', 'content', 'created', 'modified', 'marks']
        read_only_fields = ['created', 'modified']

    def to_internal_value(self, data):
        # Если тест присылает новые метки, создаем их в БД
        if 'marks' in data and isinstance(data['marks'], list):
            for mark_name in data['marks']:
                if isinstance(mark_name, str):
                    Mark.objects.get_or_create(name=mark_name)
        return super().to_internal_value(data)
"""
class CommentSerializer(serializers.ModelSerializer):
    tweetId = serializers.PrimaryKeyRelatedField(source='tweet', queryset=Tweet.objects.all())
    class Meta:
        model = Comment
        fields = ['id', 'tweetId', 'content']
"""