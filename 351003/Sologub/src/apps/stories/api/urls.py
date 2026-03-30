from rest_framework.routers import DefaultRouter
from apps.stories.api.views import StoryViewSet

router = DefaultRouter(trailing_slash=False)
router.register("stories", StoryViewSet, "stories")
urlpatterns = router.urls
