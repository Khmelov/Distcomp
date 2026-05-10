from django.urls import path
from rest_framework.routers import DefaultRouter

from apps.security.views import login_view, me_view
from apps.security.viewsets_v2 import (
    SecureMarkerViewSet,
    SecureNoteViewSet,
    SecureStoryViewSet,
    SecureWriterViewSet,
)


router = DefaultRouter(trailing_slash=False)
router.register("writers", SecureWriterViewSet, "v2-writers")
router.register("stories", SecureStoryViewSet, "v2-stories")
router.register("markers", SecureMarkerViewSet, "v2-markers")
router.register("notes", SecureNoteViewSet, "v2-notes")


urlpatterns = [
    path("login", login_view, name="v2-login"),
    path("me", me_view, name="v2-me"),
]
urlpatterns += router.urls
