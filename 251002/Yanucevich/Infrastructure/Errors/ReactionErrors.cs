using System;
using Domain.Shared;

namespace Infrastructure.Errors;

public static class ReactionErrors
{
    public static readonly Error ReactionNotFoundError =
        new Error(403, 4, 1, "Reaction with the provided ID has not been found");
    
    public static readonly Error TweetForReactionNotFoundError = 
        new Error(403, 4, 2, "Tweet with the ID provided in the reaction has not been found");
}
