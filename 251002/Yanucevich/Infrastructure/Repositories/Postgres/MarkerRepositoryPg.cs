using System;
using Dapper;
using Domain.Models;
using Domain.Repositories;
using Domain.Shared;
using Infrastructure.Errors;
using Infrastructure.Repositories.Interfaces;
using Infrastructure.Settings;
using Microsoft.Extensions.Options;
using Npgsql;

namespace Infrastructure.Repositories.Postgres;

public class MarkerRepositoryPg : PgRepository, IMarkerRepository
{
    public MarkerRepositoryPg(IOptions<InfrastructureOptions> settings) : base(settings.Value)
    {
    }

    public async Task<Result<MarkerModel>> CreateMarker(MarkerModel marker)
    {
        const string sqlInsertMarker =
        """
           INSERT 
             INTO tbl_marker
                  (
                    name
                  )
           VALUES (@Name)
        RETURNING id
        """;

        await using var connection = await GetConnection();
        await using var cmd = new NpgsqlCommand(sqlInsertMarker, connection);
        cmd.Parameters.AddWithValue("Name", marker.Name);

        try{
            using var reader = await cmd.ExecuteReaderAsync(System.Data.CommandBehavior.SingleResult);
            
            if (await reader.ReadAsync())
            {
                var markerId = reader.GetInt64(0);
                return new MarkerModel { Id = markerId, Name = marker.Name };
            }
        }
        catch (NpgsqlException ex) when (ex.SqlState == "23505"){
            return Result.Failure<MarkerModel>(MarkerErrors.MarkerNotUniqueError);
        }
        catch (NpgsqlException ex){
            return Result.Failure<MarkerModel>(Error.DatabaseError);
        }

        return Result.Failure<MarkerModel>(Error.Unknown);
    }

    public async Task<Result> DeleteMarker(long id)
    {
        const string sqlDeleteMarker = 
        """
        DELETE
          FROM tbl_marker
         WHERE id = @Id
        """;

        await using var connection = await GetConnection();

        using var command = new NpgsqlCommand(sqlDeleteMarker, connection);
        command.Parameters.AddWithValue("Id", id);

        int result;

        try{
            result = await command.ExecuteNonQueryAsync();
        }
        catch(NpgsqlException ex){
            return Result.Failure(Error.DatabaseError);
        }

        return result == 0 ? Result.Failure(MarkerErrors.MarkerNotFoundError) : Result.Success();
    }

    public async Task<Result<MarkerModel>> GetMarkerById(long id)
    {
        const string sqlGetMarkerById =
        """
        SELECT name
             , id
          FROM tbl_marker
         WHERE id = @Id
        """;

        await using var connection = await GetConnection();

        using var cmd = new NpgsqlCommand(sqlGetMarkerById, connection);
        cmd.Parameters.AddWithValue("Id", id);

        try{
            var reader = await cmd.ExecuteReaderAsync();

            if (await reader.ReadAsync())
            {
                var markerId = reader.GetInt64(reader.GetOrdinal("id"));
                var markerName = reader.GetString(reader.GetOrdinal("name"));

                return new MarkerModel { Id = markerId, Name = markerName };
            }
            else
            {
                return Result.Failure<MarkerModel>(MarkerErrors.MarkerNotFoundError);
            }
        }
        catch(NpgsqlException ex){
            return Result.Failure<MarkerModel>(Error.DatabaseError);
        }
    }

    public async Task<Result<IEnumerable<MarkerModel>>> GetMarkers()
    {
        const string sqlGetMarkers = 
        """
        SELECT name
             , id
          FROM tbl_marker
        """;

        await using var connection = await GetConnection();
        using var cmd = new NpgsqlCommand(sqlGetMarkers, connection);
    
    
        try{
            using var reader = await cmd.ExecuteReaderAsync();

            var markers = new List<MarkerModel>();

            while (await reader.ReadAsync())
            {
                var marker = new MarkerModel
                {
                    Id = reader.GetInt64(reader.GetOrdinal("id")),
                    Name = reader.GetString(reader.GetOrdinal("name"))
                };

                markers.Add(marker);
            }

            return markers;
        }
        catch(NpgsqlException ex){
            return Result.Failure<IEnumerable<MarkerModel>>(Error.DatabaseError);
        }
    }
    public async Task<Result<MarkerModel>> UpdateMarker(MarkerModel marker)
    {
        const string sqlUpdateMarker = 
        """
           UPDATE tbl_marker
              SET name = @Name
            WHERE id = @Id
        RETURNING id
        """;

        await using var connection = await GetConnection();
        using var cmd = new NpgsqlCommand(sqlUpdateMarker, connection);

        cmd.Parameters.AddWithValue("Name", marker.Name);
        cmd.Parameters.AddWithValue("Id", marker.Id);

        try {
            var returnedId = await cmd.ExecuteScalarAsync();

            if (returnedId != null && (long)returnedId == marker.Id)
            {
                return new MarkerModel{
                    Id = (long)returnedId,
                    Name = marker.Name
                };
            }

            return Result.Failure<MarkerModel>(MarkerErrors.MarkerNotFoundError);
        }
        catch(NpgsqlException ex) when (ex.SqlState == "23505"){
            return Result.Failure<MarkerModel>(MarkerErrors.MarkerNotUniqueError);
        }
        catch(NpgsqlException ex){
            return Result.Failure<MarkerModel>(Error.DatabaseError);
        }
    }
}
