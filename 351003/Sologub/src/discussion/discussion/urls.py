from django.urls import path, include

urlpatterns = [
    path("api/v1.0/", include("discussion.api.urls")),
]
