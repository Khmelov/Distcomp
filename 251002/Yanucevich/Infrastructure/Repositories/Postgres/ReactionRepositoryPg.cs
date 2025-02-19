using System;
using Domain.Models;
using Domain.Repositories;
using Domain.Shared;
using Infrastructure.Errors;
using Infrastructure.Repositories.Interfaces;
using Infrastructure.Settings;
using Microsoft.Extensions.Options;
using Npgsql;

namespace Infrastructure.Repositories.Postgres;

public class ReactionRepositoryPg : PgRepository, IReactionRepository
{
    public ReactionRepositoryPg(IOptions<InfrastructureOptions> settings) : base(settings.Value)
    {
    }

    public async Task<Result<ReactionModel>> CreateReaction(ReactionModel reaction)
    {
        const string sqlCreateReaction = 
        """
           INSERT
             INTO tbl_reaction
                  (
                    tweet_id
                  , content
                  )
           VALUES (
                    @TweetId
                  , @Content
                  )
        RETURNING id
        """;
        
        await using var connection = await GetConnection();
        await using var cmd = new NpgsqlCommand(sqlCreateReaction, connection);
        cmd.Parameters.AddWithValue("TweetId", reaction.TweetId);
        cmd.Parameters.AddWithValue("Content", reaction.Content);

        try{
            using var reader = await cmd.ExecuteReaderAsync(System.Data.CommandBehavior.SingleResult);
            
            if (await reader.ReadAsync())
            {
                var reactionId = reader.GetInt64(0);
                return new ReactionModel { 
                    Id = reactionId, 
                    TweetId = reaction.TweetId,
                    Content = reaction.Content
                };
            }
        }
        catch (NpgsqlException ex) when (ex.SqlState == "23503"){
            return Result.Failure<ReactionModel>(ReactionErrors.TweetForReactionNotFoundError);
        }
        catch (NpgsqlException ex){
            return Result.Failure<ReactionModel>(Error.DatabaseError);
        }

        return Result.Failure<ReactionModel>(Error.Unknown);

    }

    public async Task<Result> DeleteReaction(long id)
    {
        const string sqlDeleteReaction = 
        """
        DELETE
          FROM tbl_reaction
         WHERE id = @Id
        """;

        await using var connection = await GetConnection();

        using var command = new NpgsqlCommand(sqlDeleteReaction, connection);
        command.Parameters.AddWithValue("Id", id);

        int result;

        try{
            result = await command.ExecuteNonQueryAsync();
        }
        catch(NpgsqlException ex){
            return Result.Failure(Error.DatabaseError);
        }

        return result == 0 ? Result.Failure(ReactionErrors.ReactionNotFoundError) : Result.Success();

    }

    public async Task<Result<ReactionModel>> GetReactionById(long id)
    {
        const string sqlGetReactionById =
        """
        SELECT id
             , tweet_id
             , content
          FROM tbl_reaction
         WHERE id = @Id
        """;

        await using var connection = await GetConnection();

        using var cmd = new NpgsqlCommand(sqlGetReactionById, connection);
        cmd.Parameters.AddWithValue("Id", id);

        try{
            var reader = await cmd.ExecuteReaderAsync();

            if (await reader.ReadAsync())
            {
                var reactionId = reader.GetInt64(reader.GetOrdinal("id"));
                var reactionTweetId = reader.GetInt64(reader.GetOrdinal("tweet_id"));
                var reactionContent = reader.GetString(reader.GetOrdinal("content"));

                return new ReactionModel { 
                    Id = reactionId, 
                    TweetId = reactionTweetId,
                    Content = reactionContent
                };
            }
            else
            {
                return Result.Failure<ReactionModel>(ReactionErrors.ReactionNotFoundError);
            }
        }
        catch(NpgsqlException ex){
            return Result.Failure<ReactionModel>(Error.DatabaseError);
        }
    }

    public async Task<Result<IEnumerable<ReactionModel>>> GetReactions()
    {
        const string sqlGetReactions = 
        """
        SELECT id
             , tweet_id
             , content
          FROM tbl_reaction
        """;

        await using var connection = await GetConnection();
        using var cmd = new NpgsqlCommand(sqlGetReactions, connection);
    
    
        try{
            using var reader = await cmd.ExecuteReaderAsync();

            var reactions = new List<ReactionModel>();

            while (await reader.ReadAsync())
            {
                var reaction = new ReactionModel
                {
                    Id = reader.GetInt64(reader.GetOrdinal("id")),
                    TweetId = reader.GetInt64(reader.GetOrdinal("tweet_id")),
                    Content = reader.GetString(reader.GetOrdinal("content"))
                };

                reactions.Add(reaction);
            }

            return reactions;
        }
        catch(NpgsqlException ex){
            return Result.Failure<IEnumerable<ReactionModel>>(Error.DatabaseError);
        }
    }

    public async Task<Result<ReactionModel>> UpdateReaction(ReactionModel reaction)
    {
        
        const string sqlUpdateReaction = 
        """
           UPDATE tbl_reaction
              SET tweet_id = @TweetId
                , content = @Content
            WHERE id = @Id
        RETURNING id
        """;

        await using var connection = await GetConnection();
        using var cmd = new NpgsqlCommand(sqlUpdateReaction, connection);

        cmd.Parameters.AddWithValue("Id", reaction.Id);
        cmd.Parameters.AddWithValue("TweetId", reaction.TweetId);
        cmd.Parameters.AddWithValue("Content", reaction.Content);

        try {
            var returnedId = await cmd.ExecuteScalarAsync();

            if (returnedId != null && (long)returnedId == reaction.Id)
            {
                return new ReactionModel {
                    Id = (long)returnedId,
                    TweetId = reaction.TweetId,
                    Content = reaction.Content
                };
            }

            return Result.Failure<ReactionModel>(ReactionErrors.ReactionNotFoundError);
        }
        catch (NpgsqlException ex) when (ex.SqlState == "23503"){
            return Result.Failure<ReactionModel>(ReactionErrors.TweetForReactionNotFoundError);
        }
        catch(NpgsqlException ex){
            return Result.Failure<ReactionModel>(Error.DatabaseError);
        }
    }
}
