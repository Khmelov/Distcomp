import sys
import uvicorn
from fastapi import FastAPI, HTTPException
from .database import init_cassandra
from .services import DiscussionService
from .schemas import CommentRequestTo, CommentResponseTo

app = FastAPI(title="Discussion Service (Cassandra)")

@app.on_event("startup")
def startup():
    init_cassandra()

@app.post("/api/v1.0/comments", status_code=201)
def create(dto: CommentRequestTo):
    return DiscussionService.create(dto)

@app.get("/api/v1.0/comments/{id}")
def get_one(id: int):
    res = DiscussionService.get_by_id(id)
    if not res: raise HTTPException(status_code=404)
    return res

@app.get("/api/v1.0/issues/{issueId}/comments")
def get_by_issue(issueId: int):
    return DiscussionService.get_by_issue(issueId)

@app.delete("/api/v1.0/comments/{id}", status_code=204)
def delete(id: int):
    if not DiscussionService.delete(id): raise HTTPException(status_code=404)

@app.put("/api/v1.0/comments")
def update(dto: CommentRequestTo):
    res = DiscussionService.update(dto)
    if not res: raise HTTPException(status_code=404)
    return res

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=24130, http="h11", reload=True)
