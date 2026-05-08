from django.contrib import admin
from django.urls import path, include
from drf_spectacular.views import SpectacularAPIView, SpectacularSwaggerView

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/v1.0/schema/', SpectacularAPIView.as_view(), name='schema'),
    path('api/v1.0/docs/', SpectacularSwaggerView.as_view(url_name='schema'), name='swagger-ui'),
    
    # Твоя старая 5-я лаба (работает как и раньше)
    path('api/v1.0/', include('api.urls')),
    
    # --- ДОБАВЛЯЕМ НОВУЮ 6-Ю ЛАБУ ---
    path('api/v2.0/', include('api.urls_v2')), 
]