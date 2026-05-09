from django.urls import path, include
from rest_framework.routers import DefaultRouter
# ЗАМЕНИ ТЕ СТРОКИ НА ЭТУ ОДНУ:
from .views import AuthorViewSet, MarkViewSet, TweetViewSet, CommentListCreateView, CommentDetailView

router = DefaultRouter(trailing_slash=False)
router.register(r'authors', AuthorViewSet)
router.register(r'marks', MarkViewSet)
router.register(r'tweets', TweetViewSet)

urlpatterns = [
    path('', include(router.urls)),
    # Теперь .as_view() будет брать класс из того файла views.py,
    # который лежит в той же папке, что и этот urls.py
    path('comments', CommentListCreateView.as_view()),
    path('comments/<str:pk>', CommentDetailView.as_view()),
]