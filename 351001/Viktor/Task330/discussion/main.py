from fastapi import FastAPI
from Task330.discussion.api.v1.endpoints import post

app = FastAPI(title="Discussion Service", version="1.0")
app.include_router(post.router, prefix="/api/v1.0")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=24130)