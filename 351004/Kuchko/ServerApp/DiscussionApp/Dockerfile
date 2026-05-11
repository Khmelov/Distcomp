FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
USER $APP_UID
WORKDIR /app
EXPOSE 8080

ENV ASPNETCORE_URLS=http://+:8080
ENV ASPNETCORE_ENVIRONMENT=Development

FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
ARG BUILD_CONFIGURATION=Release
WORKDIR /src
COPY ["DiscussionApp.csproj", "./"]
RUN dotnet restore "DiscussionApp.csproj" 
COPY . .
RUN dotnet build "DiscussionApp.csproj" -c $BUILD_CONFIGURATION -o /app/build

FROM build AS publish   
RUN dotnet publish "./DiscussionApp.csproj" -c $BUILD_CONFIGURATION -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "DiscussionApp.dll"]
