FROM mcr.microsoft.com/dotnet/aspnet:8.0 AS base
WORKDIR /app
EXPOSE 80

FROM mcr.microsoft.com/dotnet/sdk:8.0 AS build
WORKDIR /src
COPY ["NewsPortal.csproj", "."]
RUN dotnet restore "./NewsPortal.csproj"
COPY . .
RUN dotnet build "NewsPortal.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "NewsPortal.csproj" -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "NewsPortal.dll"]