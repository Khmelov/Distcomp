using System;
using Domain.Shared;

namespace Infrastructure.Errors;

public static class TweetErrors
{
    public static readonly Error TweetNotUniqueError = 
        new Error(403, 3, 1, "Tweets must have a unique title");

    public static readonly Error TweetNotFoundError = 
        new Error(403, 3, 2, "Tweet with the provided ID has not been found");

    public static readonly Error UserForTweetNotFoundError = 
        new Error(403, 3, 3, "User with the ID provided in the tweet has not been found");
}
