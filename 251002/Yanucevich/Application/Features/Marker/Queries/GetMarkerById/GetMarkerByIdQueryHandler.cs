using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Marker.Queries;

public class GetMarkerByIdQueryHandler : IQueryHandler<GetMarkerByIdQuery, MarkerProjection>
{
    private readonly IMarkerRepository _markerRepository;

    public GetMarkerByIdQueryHandler(IMarkerRepository markerRepository)
    {
        _markerRepository = markerRepository;
    }

    public async Task<Result<MarkerProjection>> Handle(GetMarkerByIdQuery request, CancellationToken cancellationToken)
    {
        var resultMarker = await _markerRepository.GetMarkerById(request.id);
        if (!resultMarker.IsSuccess){
            return Result.Failure<MarkerProjection>(resultMarker.Error);
        }

        return resultMarker.Value.ToMarkerProjection();
    }
}
