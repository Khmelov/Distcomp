using System;
using System.Xml.Schema;
using Domain.Models;
using Domain.Repositories;
using Domain.Shared;
using Infrastructure.Errors;
using Infrastructure.Repositories.Interfaces;
using Infrastructure.Settings;
using Microsoft.Extensions.Options;
using Npgsql;

namespace Infrastructure.Repositories.Postgres;

public class TweetRepositoryPg : PgRepository, ITweetRepository
{
    public TweetRepositoryPg(IOptions<InfrastructureOptions> settings) : base(settings.Value)
    {
    }

    public async Task<Result<TweetModel>> CreateTweet(TweetModel tweet)
    {
        const string sqlCreateTweet = 
        """
           INSERT 
             INTO tbl_tweet
                  (
                    user_id
                  , title
                  , content
                  , created
                  , modified
                  )
           VALUES (
                    @UserId
                  , @Title
                  , @Content
                  , @Created
                  , @Modified
                  )
        RETURNING id
        """;

        await using var connection = await GetConnection();
        await using var cmd = new NpgsqlCommand(sqlCreateTweet, connection);

        var time = DateTime.Now;

        cmd.Parameters.AddWithValue("UserId", tweet.UserId);
        cmd.Parameters.AddWithValue("Title", tweet.Title);
        cmd.Parameters.AddWithValue("Content", tweet.Content);
        cmd.Parameters.AddWithValue("Created", time);
        cmd.Parameters.AddWithValue("Modified", time);

        try{
            using var reader = await cmd.ExecuteReaderAsync(System.Data.CommandBehavior.SingleRow);
            if (await reader.ReadAsync()){
                var tweetId = reader.GetInt64(0);
                return new TweetModel{
                    Id = tweetId,
                    UserId = tweet.UserId,
                    Title = tweet.Title,
                    Content = tweet.Content,
                    Created = time,
                    Modified = time
                };
            }
        }
        catch(NpgsqlException ex) when (ex.SqlState == "23505"){
            return Result.Failure<TweetModel>(TweetErrors.TweetNotUniqueError);
        }
        catch (NpgsqlException ex) when (ex.SqlState == "23503"){
            return Result.Failure<TweetModel>(TweetErrors.UserForTweetNotFoundError);
        }
        catch (NpgsqlException ex){
            return Result.Failure<TweetModel>(Error.DatabaseError);
        }
        return Result.Failure<TweetModel>(Error.Unknown);
    }

    public async Task<Result> DeleteTweet(long id)
    {
        const string sqlDeleteTweet = 
        """
        DELETE
          FROM tbl_tweet
         WHERE id = @Id
        """;

        await using var connection = await GetConnection();

        using var command = new NpgsqlCommand(sqlDeleteTweet, connection);
        command.Parameters.AddWithValue("Id", id);

        int result;

        try{
            result = await command.ExecuteNonQueryAsync();
        }
        catch(NpgsqlException ex){
            return Result.Failure(Error.DatabaseError);
        }
        return result == 0 ? Result.Failure(TweetErrors.TweetNotFoundError) : Result.Success();
    }

    public async Task<Result<TweetModel>> GetTweetById(long id)
    {
        const string sqlGetTweetById =
        """
        SELECT id
             , user_id
             , title
             , content
             , created
             , modified
          FROM tbl_tweet
         WHERE id = @Id
        """;

        await using var connection = await GetConnection();

        using var cmd = new NpgsqlCommand(sqlGetTweetById, connection);
        cmd.Parameters.AddWithValue("Id", id);

        try{
            var reader = await cmd.ExecuteReaderAsync();

            if (await reader.ReadAsync()){
                var tweetId = reader.GetInt64(reader.GetOrdinal("id"));
                var userId = reader.GetInt64(reader.GetOrdinal("user_id"));
                var title = reader.GetString(reader.GetOrdinal("title"));
                var content = reader.GetString(reader.GetOrdinal("content"));
                var created = reader.GetDateTime(reader.GetOrdinal("created"));
                var modified = reader.GetDateTime(reader.GetOrdinal("modified"));

                return new TweetModel{
                    Id = tweetId,
                    UserId = userId,
                    Title = title,
                    Content = content,
                    Created = created,
                    Modified = modified
                };
            }
            else{
                return Result.Failure<TweetModel>(TweetErrors.TweetNotFoundError);
            }
        }
        catch(NpgsqlException ex){
            return Result.Failure<TweetModel>(Error.DatabaseError);
        }
    }

    public async Task<Result<IEnumerable<TweetModel>>> GetTweets()
    {
        const string sqlGetTweets =
        """
        SELECT id
             , user_id
             , title
             , content
             , created
             , modified
          FROM tbl_tweet
        """;

        await using var connection = await GetConnection();

        using var cmd = new NpgsqlCommand(sqlGetTweets, connection);

        try{
            using var reader = await cmd.ExecuteReaderAsync();

            var tweets = new List<TweetModel>();

            while (await reader.ReadAsync()){
                var tweetId = reader.GetInt64(reader.GetOrdinal("id"));
                var userId = reader.GetInt64(reader.GetOrdinal("user_id"));
                var title = reader.GetString(reader.GetOrdinal("title"));
                var content = reader.GetString(reader.GetOrdinal("content"));
                var created = reader.GetDateTime(reader.GetOrdinal("created"));
                var modified = reader.GetDateTime(reader.GetOrdinal("modified"));

                tweets.Add(new TweetModel{
                    Id = tweetId,
                    UserId = userId,
                    Title = title,
                    Content = content,
                    Created = created,
                    Modified = modified
                });
            }
            return tweets;
        }
        catch(NpgsqlException ex){
            return Result.Failure<IEnumerable<TweetModel>>(Error.DatabaseError);
        }
    }

    public async Task<Result<TweetModel>> UpdateTweet(TweetModel tweet)
    {
        const string sqlUpdateTweet = 
        """
           UPDATE tbl_tweet
              SET user_id = @UserId
                , title = @Title
                , content = @Content
                , modified = @Modified
            WHERE id = @Id
        RETURNING created, id
        """;
        await using var connection = await GetConnection();
        await using var cmd = new NpgsqlCommand(sqlUpdateTweet, connection);

        var time = DateTime.Now;

        cmd.Parameters.AddWithValue("Id", tweet.Id);
        cmd.Parameters.AddWithValue("UserId", tweet.UserId);
        cmd.Parameters.AddWithValue("Title", tweet.Title);
        cmd.Parameters.AddWithValue("Content", tweet.Content);
        cmd.Parameters.AddWithValue("Modified", time);

        try{
            using var reader = await cmd.ExecuteReaderAsync(System.Data.CommandBehavior.SingleRow);
            if (await reader.ReadAsync()){
                var tweetId = reader.GetInt64(reader.GetOrdinal("id"));
                var created = reader.GetDateTime(reader.GetOrdinal("created"));
                return new TweetModel{
                    Id = tweetId,
                    UserId = tweet.UserId,
                    Title = tweet.Title,
                    Content = tweet.Content,
                    Created = created,
                    Modified = time
                };
            }
        }
        catch(NpgsqlException ex) when (ex.SqlState == "23505"){
            return Result.Failure<TweetModel>(TweetErrors.TweetNotUniqueError);
        }
        catch (NpgsqlException ex) when (ex.SqlState == "23503"){
            return Result.Failure<TweetModel>(TweetErrors.UserForTweetNotFoundError);
        }
        catch (NpgsqlException ex){
            return Result.Failure<TweetModel>(Error.DatabaseError);
        }
        return Result.Failure<TweetModel>(Error.Unknown);


    }
}
