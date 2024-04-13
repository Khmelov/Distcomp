package com.example.rv.impl.creator;

import java.util.List;

public interface CreatorMapper {

    CreatorRequestTo creatorToRequestTo(Creator creator);

    List<CreatorRequestTo> creatorToRequestTo(Iterable<Creator> creators);

    Creator dtoToEntity(CreatorRequestTo creatorRequestTo);

    List<Creator> dtoToEntity(Iterable<CreatorRequestTo> creatorRequestTos);

    CreatorResponseTo creatorToResponseTo(Creator creator);

    List<CreatorResponseTo> creatorToResponseTo(Iterable<Creator> creators);
    
    
}
