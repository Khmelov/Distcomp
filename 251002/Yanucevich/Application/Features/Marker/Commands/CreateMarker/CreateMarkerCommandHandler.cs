using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Models;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Marker.Commands;

public class CreateMarkerCommandHandler : ICommandHandler<CreateMarkerCommand, MarkerProjection>
{
    public IMarkerRepository _markerRepository;

    public CreateMarkerCommandHandler(IMarkerRepository markerRepository)
    {
        _markerRepository = markerRepository;
    }

    public async Task<Result<MarkerProjection>> Handle
        (CreateMarkerCommand request, CancellationToken cancellationToken)
    {
        MarkerModel marker = new MarkerModel(){
            Name = request.Name
        };
        var resultMarker = await _markerRepository.CreateMarker(marker);
        if (!resultMarker.IsSuccess){
            return Result.Failure<MarkerProjection>(resultMarker.Error);
        }
        return resultMarker.Value.ToMarkerProjection();
    }
}
