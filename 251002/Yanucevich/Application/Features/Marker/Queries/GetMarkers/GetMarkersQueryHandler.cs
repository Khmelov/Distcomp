using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Marker.Queries;

public class GetMarkersQueryHandler : IQueryHandler<GetMarkersQuery, IEnumerable<MarkerProjection>>
{
    private readonly IMarkerRepository _markerRepository;

    public GetMarkersQueryHandler(IMarkerRepository markerRepository){
        _markerRepository = markerRepository;
    }

    public async Task<Result<IEnumerable<MarkerProjection>>> Handle(GetMarkersQuery request, CancellationToken cancellationToken)
    {
        var resultMarkers = await _markerRepository.GetMarkers();
        if (!resultMarkers.IsSuccess){
            return Result.Failure<IEnumerable<MarkerProjection>>(resultMarkers.Error);
        }
        return Result.Success(resultMarkers.Value.Select(m => m.ToMarkerProjection()));
    }
}
