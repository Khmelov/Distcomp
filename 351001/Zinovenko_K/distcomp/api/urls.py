from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views import api_healthcheck, EditorViewSet, LabelViewSet, IssueViewSet, MessageViewSet

router = DefaultRouter(trailing_slash=False)
router.register(r'editors', EditorViewSet)
router.register(r'labels', LabelViewSet)
router.register(r'issues', IssueViewSet)
router.register(r'messages', MessageViewSet)

urlpatterns = [
    path('healthcheck/', api_healthcheck),
    path('', include(router.urls)),
]
