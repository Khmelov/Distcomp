using System;
using Domain.Models;
using Domain.Projections;

namespace Domain.Mappers;

public static class ModelToProjectionMapper
{
    public static MarkerProjection ToMarkerProjection(this MarkerModel model){
        return new MarkerProjection{
            Id = model.Id,
            Name = model.Name,
        };
    }

    public static UserProjection ToUserProjection(this UserModel model){
        return new UserProjection{
            Id = model.Id,
            Login = model.Login,
            Password = model.Password,
            Firstname = model.Firstname,
            Lastname = model.Lastname,
        };
    }

    public static TweetProjection ToTweetProjection(this TweetModel model){
        return new TweetProjection{
            Id = model.Id,
            UserId = model.UserId,
            Title = model.Title,
            Content = model.Content,
            Created = model.Created,
            Modified = model.Modified,
        };
    }

    public static ReactionProjection ToReactionProjection(this ReactionModel model){
        return new ReactionProjection{
            Id = model.Id,
            TweetId = model.TweetId,
            Content = model.Content,
        };
    }
}
