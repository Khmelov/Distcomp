from rest_framework.routers import DefaultRouter
from apps.writers.api.views import WriterViewSet

router = DefaultRouter(trailing_slash=False)
router.register("writers", WriterViewSet, "writers")
urlpatterns = router.urls
