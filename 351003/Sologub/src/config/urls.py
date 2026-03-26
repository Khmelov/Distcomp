"""
URL configuration for config project.

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/6.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path, include
from django.conf import settings

prefix_v1 = 'api/v1.0/'
urlpatterns = [
    path('admin/', admin.site.urls),
    path(prefix_v1, include('apps.markers.api.urls')),
    path(prefix_v1, include('apps.writers.api.urls')),
    path(prefix_v1, include('apps.stories.api.urls')),
    path(prefix_v1, include('apps.notes.api.urls')),
]
