from fastapi import APIRouter, Depends
from typing import List, Annotated
from src.api.v1.dependencies import get_reactions_service
from src.services.reactions import ReactionsService


from src.schemas.reactions import ReactionResponseTo, ReactionRequestToAdd, ReactionRequestToUpdate


router = APIRouter(prefix="/reactions")

@router.get("", response_model=List[ReactionResponseTo])
async def get_reactions(reactions_service: Annotated[ReactionsService, Depends(get_reactions_service)]):
    return await reactions_service.get_reactions()


@router.get("/{reaction_id}", response_model=ReactionResponseTo)
async def get_reaction_by_id(reaction_id: int, reactions_service: Annotated[ReactionsService, Depends(get_reactions_service)]):
    return await reactions_service.get_reaction_by_id(reaction_id)

@router.post("", response_model=ReactionResponseTo, status_code=201)
async def create_reaction(reaction: ReactionRequestToAdd, reactions_service: Annotated[ReactionsService, Depends(get_reactions_service)]):
    return await reactions_service.create_reaction(reaction)

@router.delete("/{reaction_id}", status_code=204)
async def delete_reaction(reaction_id: int, reactions_service: Annotated[ReactionsService, Depends(get_reactions_service)]):
    await reactions_service.delete_reaction(reaction_id)
    return reaction_id

@router.put("", response_model=ReactionResponseTo)
async def update_reaction(reaction: ReactionRequestToUpdate, reactions_service: Annotated[ReactionsService, Depends(get_reactions_service)]):
    return await reactions_service.update_reaction(reaction)
