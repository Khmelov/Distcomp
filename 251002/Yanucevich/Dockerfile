# build state
FROM mcr.microsoft.com/dotnet/sdk:9.0-alpine AS build

WORKDIR /source
COPY . .

RUN --mount=type=cache,id=nuget,target=/root/.nuget/packages \
    dotnet publish -a x64 --use-current-runtime --self-contained false -o /app ./SocialNet/SocialNet.csproj
    
#./RecognizerBrain/Brain/Brain.csproj


# state w/o build files
FROM mcr.microsoft.com/dotnet/aspnet:9.0-alpine AS final

WORKDIR /app
COPY --from=build /app .

ENTRYPOINT ["dotnet", "SocialNet.dll"]
