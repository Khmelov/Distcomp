from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from app.api.v1.router import router as v1_router
from app.api.v2.router import router as v2_router
from app.database import engine, Base
from app.kafka_client import start_out_consumer

Base.metadata.create_all(bind=engine)
start_out_consumer()

app = FastAPI(title="Distcomp API", version="2.0")


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    errors = exc.errors()
    messages = "; ".join(
        f"{'.'.join(str(loc) for loc in e['loc'])}: {e['msg']}"
        for e in errors
    )
    return JSONResponse(status_code=400, content={"errorMessage": messages, "errorCode": 40001})


app.include_router(v1_router, prefix="/api/v1.0")
app.include_router(v2_router, prefix="/api/v2.0")
