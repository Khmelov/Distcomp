using System;
using Domain.Models;
using Domain.Repositories;
using Domain.Shared;
using Infrastructure.Errors;
using Microsoft.Extensions.Logging;

namespace Infrastructure.Repositories.InMemory;

public class MarkerRepositoryInMemory : IMarkerRepository
{
    private Dictionary<long, MarkerModel> _markers = new();
    private long _id = 0;

    private ILogger<MarkerRepositoryInMemory> _logger;

    public MarkerRepositoryInMemory(ILogger<MarkerRepositoryInMemory> logger)
    {
        _logger = logger;
    }

    public Task<Result<MarkerModel>> CreateMarker(MarkerModel marker)
    {
        if (_markers.Values.Where(m => m.Name == marker.Name).Any()){
            return Task.FromResult(Result.Failure<MarkerModel>(MarkerErrors.MarkerNotUniqueError));
        }

        _markers.Add(_id, new MarkerModel{Id = _id, Name = marker.Name});
        return Task.FromResult(Result.Success(_markers[_id++]));
    }

    public Task<Result<MarkerModel>> GetMarkerById(long id)
    {
        if(_markers.TryGetValue(id, out var result)){
            return Task.FromResult(Result.Success(result));
        }
        return Task.FromResult(Result.Failure<MarkerModel>(
            MarkerErrors.MarkerNotUniqueError
            )
        );
    }

    public Task<Result<IEnumerable<MarkerModel>>> GetMarkers()
    {
        return Task.FromResult(Result.Success<IEnumerable<MarkerModel>>(_markers.Values));
    }

    public Task<Result> DeleteMarker(long id)
    {
        return Task.FromResult(
            _markers.Remove(id) 
                ? Result.Success() 
                : Result.Failure(MarkerErrors.MarkerNotFoundError));
    }

    public Task<Result<MarkerModel>> UpdateMarker(MarkerModel marker)
    {
        if(!_markers.ContainsKey(marker.Id)){
            return Task.FromResult(Result.Failure<MarkerModel>(
                MarkerErrors.MarkerNotFoundError
            ));
        }

        if (_markers.Values.Where(m => m.Name == marker.Name && m.Id != marker.Id).Any()){
            return Task.FromResult(Result.Failure<MarkerModel>(
                MarkerErrors.MarkerNotUniqueError
            ));
        }

        _markers[marker.Id] = marker;
        return Task.FromResult(Result.Success(_markers[marker.Id]));
    }
}
