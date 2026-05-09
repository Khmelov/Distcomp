from .kafka_handler import call_kafka_sync, producer, IN_TOPIC, cache_db
import json
import random
import requests
from rest_framework import status, viewsets
from rest_framework.decorators import api_view, action
from rest_framework.response import Response
from .models import Editor, Label, Issue
from .serializers import EditorSerializer, LabelSerializer, IssueSerializer

DISCUSSION_SERVICE_URL = "http://172.17.0.1:24130/api/v1.0/messages"


@api_view(['GET'])
def api_healthcheck(request):
    return Response({'status': 'ok'}, status=status.HTTP_200_OK)


class BaseCachedViewSet(viewsets.ModelViewSet):
    entity_name = ""

    def retrieve(self, request, *args, **kwargs):
        pk = kwargs.get('pk')
        cache_key = f"{self.entity_name}:{pk}"

        cached = cache_db.get(cache_key)
        if cached:
            return Response(json.loads(cached))

        response = super().retrieve(request, *args, **kwargs)

        if response.status_code == 200:
            cache_db.setex(cache_key, 300, json.dumps(response.data))

        return response

    def perform_update(self, serializer):
        instance = serializer.save()
        cache_db.delete(f"{self.entity_name}:{instance.pk}")

    def perform_destroy(self, instance):
        cache_db.delete(f"{self.entity_name}:{instance.pk}")
        instance.delete()


class EditorViewSet(BaseCachedViewSet):
    queryset = Editor.objects.all()
    serializer_class = EditorSerializer
    entity_name = "editor"

    def create(self, request, *args, **kwargs):
        login = request.data.get('login')
        if login and Editor.objects.filter(login=login).exists():
            return Response(status=status.HTTP_403_FORBIDDEN)
        return super().create(request, *args, **kwargs)


class LabelViewSet(BaseCachedViewSet):
    queryset = Label.objects.all()
    serializer_class = LabelSerializer
    entity_name = "label"


class MessageViewSet(viewsets.ViewSet):
    def list(self, request):
        resp = requests.get(DISCUSSION_SERVICE_URL, params=request.query_params, timeout=5)
        return Response(resp.json(), status=resp.status_code)

    def create(self, request):
        data = request.data.copy()
        msg_id = data.get('id', random.randint(1000, 9999))
        data['id'] = msg_id
        data['status'] = 'PENDING'

        payload = {'action': 'POST', 'data': data}
        producer.produce(IN_TOPIC, json.dumps(payload).encode('utf-8'))
        producer.flush()

        return Response(data, status=201)

    def retrieve(self, request, pk=None):
        result, status_code = call_kafka_sync('GET', {'id': pk})
        return Response(result, status=status_code)

    def update(self, request, pk=None):
        data = request.data.copy()
        data['id'] = pk
        result, status_code = call_kafka_sync('PUT', data)
        return Response(result, status=status_code)

    def destroy(self, request, pk=None):
        cache_db.delete(f"message:{pk}")
        resp = requests.delete(f"{DISCUSSION_SERVICE_URL}/{pk}", timeout=5)
        return Response(status=resp.status_code)


class IssueViewSet(BaseCachedViewSet):
    queryset = Issue.objects.all()
    serializer_class = IssueSerializer
    entity_name = "issue"

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
        cache_db.delete(f"issue:{instance.pk}")
        labels = list(instance.labels.all())
        instance.delete()
        for label in labels:
            if not label.issues.exists():
                cache_db.delete(f"label:{label.pk}")
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
        try:
            resp = requests.get(DISCUSSION_SERVICE_URL, params={'issueId': pk}, timeout=5)
            return Response(resp.json(), status=resp.status_code)
        except requests.exceptions.RequestException:
            return Response({"error": "Discussion service unavailable"}, status=503)