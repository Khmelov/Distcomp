import uvicorn


if __name__ == "__main__":
    # h11 вместо httptools: иначе часть клиентов (Java/RestAssured) даёт пустое тело POST/PUT
    uvicorn.run(
        "app.main:app",
        host="localhost",
        port=24110,
        reload=False,
        http="h11",
    )
