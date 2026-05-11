# 1. Базовый образ
FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
WORKDIR /app
# ИСПРАВЛЕНО: порт по ТЗ
EXPOSE 24130 
ENV ASPNETCORE_URLS=http://+:24130
ENV ASPNETCORE_ENVIRONMENT=Development

# 2. Сборка
FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
ARG BUILD_CONFIGURATION=Release
WORKDIR /src

# ИСПРАВЛЕНО: Правильные пути с учетом контекста "." (корень решения)
COPY ["DiscussionApp/DiscussionApp.csproj", "DiscussionApp/"]
RUN dotnet restore "DiscussionApp/DiscussionApp.csproj" 

COPY . .
# Переходим в папку проекта перед сборкой
WORKDIR "/src/DiscussionApp"
RUN dotnet build "DiscussionApp.csproj" -c $BUILD_CONFIGURATION -o /app/build

# 3. Публикация
FROM build AS publish   
RUN dotnet publish "DiscussionApp.csproj" -c $BUILD_CONFIGURATION -o /app/publish /p:UseAppHost=false

# 4. Финальный запуск
FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "DiscussionApp.dll"]