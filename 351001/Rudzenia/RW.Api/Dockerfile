FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
WORKDIR /app
EXPOSE 24110

FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src
COPY ["RW.Api/RW.Api.csproj", "RW.Api/"]
COPY ["RW.Application/RW.Application.csproj", "RW.Application/"]
COPY ["RW.Domain/RW.Domain.csproj", "RW.Domain/"]
COPY ["RW.Infrastructure/RW.Infrastructure.csproj", "RW.Infrastructure/"]
RUN dotnet restore "RW.Api/RW.Api.csproj"
COPY . .
WORKDIR "/src/RW.Api"
RUN dotnet publish -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "RW.Api.dll"]
