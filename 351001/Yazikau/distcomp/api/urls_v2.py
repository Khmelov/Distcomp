# api/urls_v2.py
from django.urls import path, include
from rest_framework.routers import DefaultRouter
from .views_v2 import AuthorViewSetV2, LoginView, ProtectedResourceView, AdminEndpointView

router_v2 = DefaultRouter(trailing_slash=False)
router_v2.register(r'authors', AuthorViewSetV2, basename='authors-v2')

urlpatterns = [
    path('', include(router_v2.urls)),
    path('login', LoginView.as_view()),
    path('protected-resource', ProtectedResourceView.as_view()),
    path('admin-endpoint', AdminEndpointView.as_view()),
]