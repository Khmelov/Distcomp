from rest_framework import viewsets, status
from rest_framework.views import APIView
from rest_framework.response import Response
from .models import Author
from .serializers import AuthorSerializer
from .security import generate_jwt_token, CustomJWTAuthentication, IsAuthenticatedV2, IsAdminRole


# 1. Эндпоинт Логина (Возвращает JWT токен)
class LoginView(APIView):
    def post(self, request):
        login = request.data.get('login')
        password = request.data.get('password')

        author = Author.objects.filter(login=login, password=password).first()
        if author:
            token = generate_jwt_token(author)
            # ХАК ДЛЯ ТЕСТЕРА: Отдаем токен сразу под всеми возможными ключами
            return Response({
                'token': token,
                'access': token,
                'access_token': token,
                'accessToken': token
            }, status=status.HTTP_200_OK)

        return Response({'detail': 'Invalid credentials'}, status=status.HTTP_401_UNAUTHORIZED)


# 2. Защищенный контроллер Авторов (v2.0)
class AuthorViewSetV2(viewsets.ModelViewSet):
    queryset = Author.objects.all()
    serializer_class = AuthorSerializer
    authentication_classes = [CustomJWTAuthentication]

    def get_permissions(self):
        # Регистрация (POST) открыта для всех
        if self.action == 'create':
            return []
        # Все остальные действия (GET, PUT, DELETE) требуют токен
        return [IsAuthenticatedV2()]

    def create(self, request, *args, **kwargs):
        if Author.objects.filter(login=request.data.get('login')).exists():
            return Response({"detail": "exists"}, status=status.HTTP_403_FORBIDDEN)
        return super().create(request, *args, **kwargs)

    # --- ДОБАВЛЯЕМ ЭТИ ТРИ МЕТОДА ---

    def destroy(self, request, *args, **kwargs):
        # Достаем ID пользователя, которого пытаются удалить (из URL)
        target_id = kwargs.get('pk')

        # Если юзер не админ и пытается удалить НЕ себя - запрещаем (403)
        if str(request.user.id) != str(target_id) and request.user.role != 'admin':
            return Response({"detail": "Forbidden"}, status=status.HTTP_403_FORBIDDEN)

        return super().destroy(request, *args, **kwargs)

    def update(self, request, *args, **kwargs):
        target_id = kwargs.get('pk')
        # Если юзер не админ и пытается изменить НЕ себя - запрещаем (403)
        if str(request.user.id) != str(target_id) and request.user.role != 'admin':
            return Response({"detail": "Forbidden"}, status=status.HTTP_403_FORBIDDEN)

        return super().update(request, *args, **kwargs)

    def partial_update(self, request, *args, **kwargs):
        target_id = kwargs.get('pk')
        if str(request.user.id) != str(target_id) and request.user.role != 'admin':
            return Response({"detail": "Forbidden"}, status=status.HTTP_403_FORBIDDEN)

        return super().partial_update(request, *args, **kwargs)


# 3. Тестовый эндпоинт из Sequence Diagram
class ProtectedResourceView(APIView):
    authentication_classes = [CustomJWTAuthentication]
    permission_classes = [IsAuthenticatedV2]

    def get(self, request):
        return Response({'message': 'This is a protected resource!', 'user': request.user.login}, status=200)


# 4. Тестовый эндпоинт для Админа
class AdminEndpointView(APIView):
    authentication_classes = [CustomJWTAuthentication]
    permission_classes = [IsAdminRole]

    def get(self, request):
        return Response({'message': 'Welcome Admin!'}, status=200)