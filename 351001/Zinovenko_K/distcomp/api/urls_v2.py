from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views_v2 import (
    login_editor,
    ProtectedEditorViewSet, ProtectedLabelViewSet,
    ProtectedIssueViewSet, ProtectedMessageViewSet,
)

router = DefaultRouter(trailing_slash=False)
router.register(r'editors', ProtectedEditorViewSet)
router.register(r'labels', ProtectedLabelViewSet)
router.register(r'issues', ProtectedIssueViewSet)
router.register(r'messages', ProtectedMessageViewSet, basename='message')

urlpatterns = [
    path('login', login_editor, name='v2-login'),
    path('', include(router.urls)),
]
