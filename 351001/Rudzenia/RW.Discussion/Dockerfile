FROM mcr.microsoft.com/dotnet/aspnet:9.0 AS base
WORKDIR /app
EXPOSE 24130

FROM mcr.microsoft.com/dotnet/sdk:9.0 AS build
WORKDIR /src
COPY ["RW.Discussion/RW.Discussion.csproj", "RW.Discussion/"]
RUN dotnet restore "RW.Discussion/RW.Discussion.csproj"
COPY . .
WORKDIR "/src/RW.Discussion"
RUN dotnet publish -c Release -o /app/publish

FROM base AS final
WORKDIR /app
COPY --from=build /app/publish .
ENTRYPOINT ["dotnet", "RW.Discussion.dll"]
