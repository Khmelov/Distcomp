import json
import random
import requests
import bcrypt

from rest_framework import status, viewsets
from rest_framework.decorators import api_view, action
from rest_framework.response import Response

from .models import Editor, Label, Issue
from .serializers import EditorSerializer, LabelSerializer, IssueSerializer
from .authentication import JWTAuthentication, EditorUser
from .permissions import (
    IsAuthenticated, EditorPermission, IssuePermission,
    IsAdminOrReadOnly, MessagePermission
)
from .jwt_utils import generate_token
from .exceptions import error_response
from .kafka_handler import call_kafka_sync, producer, IN_TOPIC, cache_db

DISCUSSION_SERVICE_URL = "http://172.17.0.1:24130/api/v1.0/messages"


def hash_password(raw_password):
    return bcrypt.hashpw(raw_password.encode('utf-8'), bcrypt.gensalt()).decode('utf-8')


def check_password(raw_password, hashed_password):
    return bcrypt.checkpw(raw_password.encode('utf-8'), hashed_password.encode('utf-8'))


@api_view(['POST'])
def login_editor(request):
    """POST /api/v2.0/login - authentication"""
    data = request.data
    login = data.get('login')
    password = data.get('password')

    if not login or not password:
        return error_response(
            'Fields login and password are required',
            status.HTTP_400_BAD_REQUEST
        )

    try:
        editor = Editor.objects.get(login=login)
    except Editor.DoesNotExist:
        return error_response('Invalid login or password', status.HTTP_401_UNAUTHORIZED)

    if not check_password(password, editor.password):
        return error_response('Invalid login or password', status.HTTP_401_UNAUTHORIZED)

    token = generate_token(editor)

    return Response({
        'access_token': token,
    }, status=status.HTTP_200_OK)


class ProtectedEditorViewSet(viewsets.ModelViewSet):
    queryset = Editor.objects.all()
    serializer_class = EditorSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated, EditorPermission]
    entity_name = "editor"

    def initialize_request(self, request, *args, **kwargs):
        """Override to skip auth for registration (POST without pk)."""
        # If it's a POST to the list endpoint (no pk), it's registration
        if request.method == 'POST' and not kwargs.get('pk'):
            self.authentication_classes = []
            self.permission_classes = []
        else:
            self.authentication_classes = [JWTAuthentication]
            self.permission_classes = [IsAuthenticated, EditorPermission]
        return super().initialize_request(request, *args, **kwargs)

    def list(self, request, *args, **kwargs):
        editors = Editor.objects.all()
        data = []
        for editor in editors:
            data.append({
                'id': editor.id,
                'login': editor.login,
                'firstname': editor.firstname,
                'lastname': editor.lastname,
                'role': editor.role,
            })
        return Response(data)

    def retrieve(self, request, *args, **kwargs):
        pk = kwargs.get('pk')
        cache_key = f"editor:{pk}"
        cached = cache_db.get(cache_key)
        if cached:
            return Response(json.loads(cached))

        try:
            editor = Editor.objects.get(pk=pk)
        except Editor.DoesNotExist:
            return error_response('Editor not found', status.HTTP_404_NOT_FOUND)

        self.check_object_permissions(request, editor)

        data = {
            'id': editor.id,
            'login': editor.login,
            'firstname': editor.firstname,
            'lastname': editor.lastname,
            'role': editor.role,
        }
        cache_db.setex(cache_key, 300, json.dumps(data))
        return Response(data)

    def create(self, request, *args, **kwargs):
        """POST /api/v2.0/editors - registration (no auth required)"""
        data = request.data

        login = data.get('login')
        password = data.get('password')
        firstname = data.get('firstname')
        lastname = data.get('lastname')
        role = data.get('role', 'CUSTOMER')

        if not login or not password or not firstname or not lastname:
            return error_response(
                'Fields login, password, firstname, lastname are required',
                status.HTTP_400_BAD_REQUEST
            )

        if len(login) < 2:
            return error_response('login must be at least 2 characters', status.HTTP_400_BAD_REQUEST)
        if len(password) < 8:
            return error_response('password must be at least 8 characters', status.HTTP_400_BAD_REQUEST)
        if len(firstname) < 2:
            return error_response('firstname must be at least 2 characters', status.HTTP_400_BAD_REQUEST)
        if len(lastname) < 2:
            return error_response('lastname must be at least 2 characters', status.HTTP_400_BAD_REQUEST)

        if Editor.objects.filter(login=login).exists():
            return error_response('Login already exists', status.HTTP_403_FORBIDDEN)

        if role not in ('ADMIN', 'CUSTOMER'):
            role = 'CUSTOMER'

        hashed = hash_password(password)

        editor = Editor.objects.create(
            login=login,
            password=hashed,
            firstname=firstname,
            lastname=lastname,
            role=role,
        )

        return Response({
            'id': editor.id,
            'login': editor.login,
            'firstname': editor.firstname,
            'lastname': editor.lastname,
            'role': editor.role,
        }, status=status.HTTP_201_CREATED)

    def update(self, request, *args, **kwargs):
        pk = kwargs.get('pk')

        # CUSTOMER can only update their own profile
        if request.user.role != 'ADMIN' and int(pk) != request.user.id:
            return error_response('Forbidden', status.HTTP_403_FORBIDDEN)

        try:
            editor = Editor.objects.get(pk=pk)
        except Editor.DoesNotExist:
            return error_response('Editor not found', status.HTTP_404_NOT_FOUND)

        self.check_object_permissions(request, editor)

        data = request.data.copy()
        if 'password' in data and data['password']:
            data['password'] = hash_password(data['password'])

        serializer = self.get_serializer(editor, data=data, partial=kwargs.get('partial', False))
        serializer.is_valid(raise_exception=True)
        serializer.save()
        cache_db.delete(f"editor:{pk}")
        return Response(serializer.data)

    def partial_update(self, request, *args, **kwargs):
        kwargs['partial'] = True
        return self.update(request, *args, **kwargs)

    def destroy(self, request, *args, **kwargs):
        pk = kwargs.get('pk')

        # CUSTOMER can only delete their own profile
        if request.user.role != 'ADMIN' and int(pk) != request.user.id:
            return error_response('Forbidden', status.HTTP_403_FORBIDDEN)

        try:
            editor = Editor.objects.get(pk=pk)
        except Editor.DoesNotExist:
            return error_response('Editor not found', status.HTTP_404_NOT_FOUND)

        self.check_object_permissions(request, editor)
        cache_db.delete(f"editor:{pk}")
        editor.delete()
        return Response(status=status.HTTP_204_NO_CONTENT)


class ProtectedLabelViewSet(viewsets.ModelViewSet):
    queryset = Label.objects.all()
    serializer_class = LabelSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated, IsAdminOrReadOnly]
    entity_name = "label"

    def retrieve(self, request, *args, **kwargs):
        pk = kwargs.get('pk')
        cache_key = f"label:{pk}"
        cached = cache_db.get(cache_key)
        if cached:
            return Response(json.loads(cached))

        response = super().retrieve(request, *args, **kwargs)
        if response.status_code == 200:
            cache_db.setex(cache_key, 300, json.dumps(response.data))
        return response

    def perform_update(self, serializer):
        instance = serializer.save()
        cache_db.delete(f"label:{instance.pk}")

    def perform_destroy(self, instance):
        cache_db.delete(f"label:{instance.pk}")
        instance.delete()


class ProtectedIssueViewSet(viewsets.ModelViewSet):
    queryset = Issue.objects.all()
    serializer_class = IssueSerializer
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated, IssuePermission]
    entity_name = "issue"

    def create(self, request, *args, **kwargs):
        data = request.data.copy()
        editor_id = data.get('editorId')
        title = data.get('title')

        # CUSTOMER can only create issues for themselves
        if request.user.role == 'CUSTOMER':
            if editor_id and int(editor_id) != request.user.id:
                return error_response(
                    'CUSTOMER can only create issues for themselves',
                    status.HTTP_403_FORBIDDEN
                )
            if not editor_id:
                data['editorId'] = request.user.id

        if editor_id and title:
            already_exists = Issue.objects.filter(
                editor_id=editor_id,
                title=title
            ).exists()
            if already_exists:
                return error_response('Issue with this title already exists for this editor',
                                      status.HTTP_403_FORBIDDEN)

        serializer = self.get_serializer(data=data)
        serializer.is_valid(raise_exception=True)
        serializer.save()
        return Response(serializer.data, status=status.HTTP_201_CREATED)

    def update(self, request, *args, **kwargs):
        instance = self.get_object()
        self.check_object_permissions(request, instance)
        return super().update(request, *args, **kwargs)

    def partial_update(self, request, *args, **kwargs):
        instance = self.get_object()
        self.check_object_permissions(request, instance)
        return super().partial_update(request, *args, **kwargs)

    def destroy(self, request, *args, **kwargs):
        instance = self.get_object()
        self.check_object_permissions(request, instance)
        return super().destroy(request, *args, **kwargs)

    def retrieve(self, request, *args, **kwargs):
        pk = kwargs.get('pk')
        cache_key = f"issue:{pk}"
        cached = cache_db.get(cache_key)
        if cached:
            return Response(json.loads(cached))

        response = super().retrieve(request, *args, **kwargs)
        if response.status_code == 200:
            cache_db.setex(cache_key, 300, json.dumps(response.data))
        return response

    def perform_update(self, serializer):
        instance = serializer.save()
        cache_db.delete(f"issue:{instance.pk}")

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
            return error_response('Discussion service unavailable',
                                  status.HTTP_503_SERVICE_UNAVAILABLE)


class ProtectedMessageViewSet(viewsets.ViewSet):
    authentication_classes = [JWTAuthentication]
    permission_classes = [IsAuthenticated, MessagePermission]

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

        return Response(data, status=status.HTTP_201_CREATED)

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
