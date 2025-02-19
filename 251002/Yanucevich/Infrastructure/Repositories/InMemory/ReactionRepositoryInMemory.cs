using System;
using Domain.Models;
using Domain.Repositories;
using Domain.Shared;
using Infrastructure.Errors;

namespace Infrastructure.Repositories.InMemory;

public class ReactionRepositoryInMemory : IReactionRepository
{
    private Dictionary<long, ReactionModel> _reactions = new ();
    private long _id = 0;

    public Task<Result<ReactionModel>> CreateReaction(ReactionModel reaction)
    {
        _reactions.Add(_id, new ReactionModel{
            Id = _id,
            TweetId = reaction.TweetId,
            Content = reaction.Content
        });

        return Task.FromResult(Result.Success(_reactions[_id++]));
    }

    public Task<Result> DeleteReaction(long id)
    {
        return Task.FromResult(
            _reactions.Remove(id)
                ? Result.Success()
                : Result.Failure(ReactionErrors.ReactionNotFoundError)
        );
    }

    public Task<Result<ReactionModel>> GetReactionById(long id)
    {
        return Task.FromResult(
            _reactions.TryGetValue(id, out ReactionModel model)
                ? Result.Success(model)
                : Result.Failure<ReactionModel>(ReactionErrors.ReactionNotFoundError)
        );
    }

    public Task<Result<IEnumerable<ReactionModel>>> GetReactions()
    {
        return Task.FromResult(Result.Success<IEnumerable<ReactionModel>>(_reactions.Values));
    }

    public Task<Result<ReactionModel>> UpdateReaction(ReactionModel reaction)
    {
        if(!_reactions.ContainsKey(reaction.Id)){
            return Task.FromResult(Result.Failure<ReactionModel>(
                ReactionErrors.ReactionNotFoundError
            ));
        }

        _reactions[reaction.Id] = reaction;
        return Task.FromResult(Result.Success(_reactions[reaction.Id]));
    }
}