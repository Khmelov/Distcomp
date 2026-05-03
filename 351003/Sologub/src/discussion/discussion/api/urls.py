from django.urls import path, include
from rest_framework.routers import DefaultRouter
from . import views

router = DefaultRouter(trailing_slash=False)
router.register("notes", views.NoteViewSet, basename="notes")
urlpatterns = router.urls
