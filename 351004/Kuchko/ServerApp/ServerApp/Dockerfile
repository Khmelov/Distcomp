# 1. Слой выполнения (Runtime)
FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
WORKDIR /app
# ВАЖНО: Порт из ТЗ
EXPOSE 24110

# Настройка портов и среды внутри контейнера
ENV ASPNETCORE_URLS=http://+:24110
ENV ASPNETCORE_ENVIRONMENT=Development

# 2. Слой сборки (SDK)
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
ARG BUILD_CONFIGURATION=Release
WORKDIR /src

# Копируем файл проекта и восстанавливаем зависимости
COPY ["ServerApp.csproj", "."]
RUN dotnet restore "./ServerApp.csproj"

# Копируем все файлы и собираем проект
COPY . .
WORKDIR "/src/."
RUN dotnet build "ServerApp.csproj" -c $BUILD_CONFIGURATION -o /app/build

# 3. Слой публикации
FROM build AS publish
RUN dotnet publish "ServerApp.csproj" -c $BUILD_CONFIGURATION -o /app/publish /p:UseAppHost=false

# 4. Финальный образ
FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "ServerApp.dll"]