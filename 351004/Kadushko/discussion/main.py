from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from app.api.v1.router import router
from app.database import init_db

print(1)

try:
    init_db()
    print("DB OK")
except Exception as e:
    print("DB ERROR:", e)
    raise

app = FastAPI(title="Discussion API", version="1.0")

@app.exception_handler(RequestValidationError)
async def validation_exception_handler(request: Request, exc: RequestValidationError):
    errors = exc.errors()
    messages = "; ".join(
        f"{'.'.join(str(loc) for loc in e['loc'])}: {e['msg']}"
        for e in errors
    )
    return JSONResponse(status_code=400, content={"errorMessage": messages, "errorCode": 40001})

app.include_router(router, prefix="/api/v1.0")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="localhost", port=24130, reload=False)