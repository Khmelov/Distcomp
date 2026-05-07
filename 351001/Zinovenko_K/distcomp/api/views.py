from rest_framework import status, viewsets
from rest_framework.decorators import api_view
from rest_framework.exceptions import PermissionDenied
from rest_framework.response import Response
from rest_framework.decorators import action
from .models import Editor, Label, Issue, Message
from .serializers import EditorSerializer, LabelSerializer, IssueSerializer, MessageSerializer

@api_view(['GET'])
def api_healthcheck(request):
    return Response({'status': 'ok'}, status=status.HTTP_200_OK)

class EditorViewSet(viewsets.ModelViewSet):
    queryset = Editor.objects.all()
    serializer_class = EditorSerializer

    def create(self, request, *args, **kwargs):
        login = request.data.get('login')
        if login and Editor.objects.filter(login=login).exists():
            return Response(status=status.HTTP_403_FORBIDDEN)
        return super().create(request, *args, **kwargs)

class LabelViewSet(viewsets.ModelViewSet):
    queryset = Label.objects.all()
    serializer_class = LabelSerializer

class MessageViewSet(viewsets.ModelViewSet):
    queryset = Message.objects.all()
    serializer_class = MessageSerializer

class IssueViewSet(viewsets.ModelViewSet):
    queryset = Issue.objects.all()
    serializer_class = IssueSerializer

    def create(self, request, *args, **kwargs):
        editor_id = request.data.get('editorId')
        title = request.data.get('title')
        if editor_id and title:
            already_exists = Issue.objects.filter(
                editor_id=editor_id,
                title=title
            ).exists()
            if already_exists:
                return Response(status=status.HTTP_403_FORBIDDEN)
        return super().create(request, *args, **kwargs)

    def perform_destroy(self, instance):
        labels = list(instance.labels.all())
        instance.delete()
        for label in labels:
            if not label.issues.exists():
                label.delete()

    def get_queryset(self):
        queryset = Issue.objects.all()
        labels_ids = self.request.query_params.getlist('label_ids')
        label_names = self.request.query_params.getlist('label_names')
        editor_login = self.request.query_params.get('editor_login')
        title = self.request.query_params.get('title')
        content = self.request.query_params.get('content')

        if labels_ids:
            queryset = queryset.filter(labels__id__in=labels_ids)
        if label_names:
            queryset = queryset.filter(labels__name__in=label_names)
        if editor_login:
            queryset = queryset.filter(editor__login=editor_login)
        if title:
            queryset = queryset.filter(title__icontains=title)
        if content:
            queryset = queryset.filter(content__icontains=content)

        return queryset.distinct()

    @action(detail=True, methods=['get'], url_path='editor')
    def get_editor(self, request, pk=None):
        issue = self.get_object()
        serializer = EditorSerializer(issue.editor)
        return Response(serializer.data)

    @action(detail=True, methods=['get'], url_path='labels')
    def get_labels(self, request, pk=None):
        issue = self.get_object()
        serializer = LabelSerializer(issue.labels.all(), many=True)
        return Response(serializer.data)

    @action(detail=True, methods=['get'], url_path='messages')
    def get_messages(self, request, pk=None):
        issue = self.get_object()
        messages = Message.objects.filter(issue=issue)
        serializer = MessageSerializer(messages, many=True)
        return Response(serializer.data)
