from rest_framework import serializers
from .models import Editor, Label, Issue, Message

class EditorSerializer(serializers.ModelSerializer):
    class Meta:
        model = Editor
        fields = ['id', 'login', 'password', 'firstname', 'lastname']

class LabelSerializer(serializers.ModelSerializer):
    class Meta:
        model = Label
        fields = ['id', 'name']

class MessageSerializer(serializers.ModelSerializer):
    issueId = serializers.PrimaryKeyRelatedField(
        source='issue',
        queryset=Issue.objects.all()
    )

    class Meta:
        model = Message
        fields = ['id', 'issueId', 'content']

class IssueSerializer(serializers.ModelSerializer):
    created = serializers.DateTimeField(read_only=True)
    modified = serializers.DateTimeField(read_only=True)
    editorId = serializers.PrimaryKeyRelatedField(
        source='editor',
        queryset=Editor.objects.all()
    )

    def create(self, validated_data):
        labels_data = self.context['request'].data.get('labels', [])
        issue = Issue.objects.create(**validated_data)
        for label_name in labels_data:
            if isinstance(label_name, str):
                label_obj, _ = Label.objects.get_or_create(name=label_name)
                issue.labels.add(label_obj)
            else:
                issue.labels.add(label_name)
        return issue

    class Meta:
        model = Issue
        fields = ['id', 'editorId', 'title', 'content', 'created', 'modified']
