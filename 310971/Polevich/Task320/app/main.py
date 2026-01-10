import uvicorn

if __name__ == "__main__":
    uvicorn.run("app.api:app", host="localhost", port=24110, reload=False)
#docker-compose up -d