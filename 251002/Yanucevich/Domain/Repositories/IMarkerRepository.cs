using System;
using Domain.Models;
using Domain.Shared;

namespace Domain.Repositories;

public interface IMarkerRepository
{
    Task<Result<MarkerModel>> CreateMarker(MarkerModel marker);
    Task<Result<IEnumerable<MarkerModel>>> GetMarkers();
    Task<Result<MarkerModel>> GetMarkerById(long id);
    Task<Result<MarkerModel>> UpdateMarker(MarkerModel marker);
    Task<Result> DeleteMarker(long id);
}
