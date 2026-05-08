from django.urls import path
from .views import MessageView

urlpatterns = [
    path('messages', MessageView.as_view()),
    path('messages/<str:pk>', MessageView.as_view()),
]
