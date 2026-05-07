from rest_framework.routers import DefaultRouter
from apps.notes.api.views import NoteViewSet

router = DefaultRouter(trailing_slash=False)
router.register('notes', NoteViewSet, 'notes')
urlpatterns = router.urls
