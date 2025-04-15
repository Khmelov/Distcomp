
from fastapi import APIRouter, HTTPException

from src.schemas.reactions import ReactionResponseTo, ReactionRequestToAdd, ReactionRequestToUpdate
from src.repositories import reactions

router = APIRouter(prefix="/api/v1.0/reactions", tags=["discussion"])

@router.get("", response_model=list[ReactionResponseTo])
async def get_reactions():
    data = reactions.get_all_reactions()
    return [ReactionResponseTo(**d) for d in data]

@router.get("/{reaction_id}", response_model=ReactionResponseTo)
async def get_reaction_by_id(reaction_id: int):
    row = reactions.get_reaction_by_id(reaction_id)
    if not row:
        raise HTTPException(status_code=404, detail="Reaction not found")
    return ReactionResponseTo(**row)

@router.post("", response_model=ReactionResponseTo, status_code=201)
async def create_reaction(payload: ReactionRequestToAdd):
    created = reactions.create_reaction(payload.storyId, payload.content)
    return ReactionResponseTo(**created)

@router.delete("/{reaction_id}", status_code=204)
async def delete_reaction(reaction_id: int):
    row = reactions.get_reaction_by_id(reaction_id)
    if not row:
        raise HTTPException(status_code=404, detail="Reaction not found")
    reactions.delete_reaction(reaction_id)
    return

@router.put("", response_model=ReactionResponseTo)
async def update_reaction(payload: ReactionRequestToUpdate):
    row = reactions.get_reaction_by_id(payload.id)
    if not row:
        raise HTTPException(status_code=404, detail="Reaction not found")
    updated = reactions.update_reaction(payload.id, payload.storyId, payload.content, "APPROVE")
    return ReactionResponseTo(**updated)
