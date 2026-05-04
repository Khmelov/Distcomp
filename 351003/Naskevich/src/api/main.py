import uvicorn

from fastapi import FastAPI

from src.api.posts_kafka import posts_kafka_router
from src.api.v1 import editors, markers, posts, tweets
from src.api.v2 import auth as v2_auth
from src.api.v2 import editors as v2_editors
from src.api.v2 import markers as v2_markers
from src.api.v2 import posts as v2_posts
from src.api.v2 import tweets as v2_tweets
from src.api.v2.errors import install_v2_exception_handlers
from src.database.tables import run_mappers

run_mappers()

app = FastAPI(title="REST API Lab")
app.include_router(posts_kafka_router)

install_v2_exception_handlers(app)

API_V1 = "/api/v1.0"
API_V2 = "/api/v2.0"

app.include_router(editors.router, prefix=API_V1)
app.include_router(tweets.router, prefix=API_V1)
app.include_router(markers.router, prefix=API_V1)
app.include_router(posts.router, prefix=API_V1)

app.include_router(v2_auth.router, prefix=API_V2)
app.include_router(v2_editors.router, prefix=API_V2)
app.include_router(v2_tweets.router, prefix=API_V2)
app.include_router(v2_markers.router, prefix=API_V2)
app.include_router(v2_posts.router, prefix=API_V2)


if __name__ == "__main__":
    uvicorn.run("src.api.main:app", host="0.0.0.0", port=24110, log_level="info")
