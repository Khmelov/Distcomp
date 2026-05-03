"""URL configuration for config project."""
from django.contrib import admin
from django.urls import path, include

prefix_v1 = 'api/v1.0/'
prefix_v2 = 'api/v2.0/'

urlpatterns = [
    path('admin/', admin.site.urls),
    # v1.0 — unprotected (kept for backward compatibility).
    path(prefix_v1, include('apps.markers.api.urls')),
    path(prefix_v1, include('apps.writers.api.urls')),
    path(prefix_v1, include('apps.stories.api.urls')),
    path(prefix_v1, include('apps.notes.api.urls')),
    # v2.0 — JWT-protected, role-based.
    path(prefix_v2, include('apps.security.urls')),
]
