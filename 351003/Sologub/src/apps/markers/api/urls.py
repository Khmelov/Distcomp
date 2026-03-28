from rest_framework.routers import DefaultRouter
from apps.markers.api.views import MarkerViewSet

router = DefaultRouter(trailing_slash=False)
router.register('markers', MarkerViewSet, 'markers')
urlpatterns = router.urls
