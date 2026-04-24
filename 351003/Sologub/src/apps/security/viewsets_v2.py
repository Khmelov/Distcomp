"""v2.0 secured view-sets.

We re-use the v1.0 view-sets' business logic and attach JWT authentication
plus role-based permissions.
"""
from rest_framework import status
from rest_framework.response import Response

from apps.security.authentication import JWTAuthentication
from apps.security.passwords import hash_password
from apps.security.permissions import (
    IsAuthenticatedJWT,
    OwnedResourcePermission,
    ReadOnlyForCustomer,
    WriterPermission,
    _is_admin,
)
from apps.security.serializers import RegisterSerializer, WriterSecureSerializer
from apps.markers.api.views import MarkerViewSet
from apps.notes.api.views import NoteViewSet
from apps.stories.api.views import StoryViewSet
from apps.writers.api.views import WriterViewSet
from apps.writers.models import Writer


class SecureWriterViewSet(WriterViewSet):
    """Writers endpoint.

    * POST /api/v2.0/writers is public registration (no JWT required).
    * All other methods require a valid JWT and follow the role policy:
      - ADMIN: full access.
      - CUSTOMER: read anyone, modify/delete only self.
    """

    authentication_classes = (JWTAuthentication,)

    def get_permissions(self):
        # POST /writers is public registration — skip permission checks.
        if self.action == "create":
            return []
        return [WriterPermission()]

    def get_serializer_class(self):
        if self.action == "create":
            return RegisterSerializer
        return WriterSecureSerializer

    def create(self, request, *args, **kwargs):
        serializer = RegisterSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        requested_role = serializer.validated_data.get("role", Writer.ROLE_CUSTOMER)

        writer = Writer.objects.create(
            login=serializer.validated_data["login"],
            password=hash_password(serializer.validated_data["password"]),
            firstname=serializer.validated_data["firstname"],
            lastname=serializer.validated_data["lastname"],
            role=requested_role,
        )
        return Response(
            WriterSecureSerializer(writer).data,
            status=status.HTTP_201_CREATED,
        )

    def update(self, request, *args, **kwargs):
        response = super().update(request, *args, **kwargs)
        new_password = request.data.get("password")
        if response.status_code < 400 and new_password:
            instance = self.get_object()
            instance.password = hash_password(new_password)
            instance.save(update_fields=["password"])
        return response


class SecureStoryViewSet(StoryViewSet):
    authentication_classes = (JWTAuthentication,)
    permission_classes = (OwnedResourcePermission,)


class SecureMarkerViewSet(MarkerViewSet):
    authentication_classes = (JWTAuthentication,)
    permission_classes = (ReadOnlyForCustomer,)


class SecureNoteViewSet(NoteViewSet):
    """Secured wrapper around the cache-backed NoteViewSet.

    Because notes live in Redis (not the ORM), ownership is checked against
    the payload / cached object directly.
    """

    authentication_classes = (JWTAuthentication,)
    permission_classes = (IsAuthenticatedJWT,)

    def _writer_id_from_story(self, story_id):
        from apps.stories.models import Story
        try:
            return Story.objects.get(pk=int(story_id)).writerId_id
        except (Story.DoesNotExist, TypeError, ValueError):
            return None

    def _assert_owner_or_admin(self, request, story_id):
        if _is_admin(request.user):
            return None
        owner_id = self._writer_id_from_story(story_id)
        if owner_id is None or int(owner_id) != int(request.user.id):
            return Response(
                {"detail": "You may only modify notes attached to your own stories."},
                status=status.HTTP_403_FORBIDDEN,
            )
        return None

    def create(self, request, *args, **kwargs):
        story_id = request.data.get("storyId") if isinstance(request.data, dict) else None
        forbidden = self._assert_owner_or_admin(request, story_id)
        if forbidden is not None:
            return forbidden
        return super().create(request, *args, **kwargs)

    def update(self, request, *args, **kwargs):
        story_id = request.data.get("storyId") if isinstance(request.data, dict) else None
        forbidden = self._assert_owner_or_admin(request, story_id)
        if forbidden is not None:
            return forbidden
        return super().update(request, *args, **kwargs)

    def partial_update(self, request, *args, **kwargs):
        return self.update(request, *args, **kwargs)

    def destroy(self, request, *args, **kwargs):
        from apps.notes import cache as notes_cache
        pk = kwargs.get("pk")
        try:
            note = notes_cache.get(int(pk)) if pk is not None else None
        except Exception:
            note = None
        if note is not None:
            forbidden = self._assert_owner_or_admin(request, note.get("storyId"))
            if forbidden is not None:
                return forbidden
        return super().destroy(request, *args, **kwargs)
