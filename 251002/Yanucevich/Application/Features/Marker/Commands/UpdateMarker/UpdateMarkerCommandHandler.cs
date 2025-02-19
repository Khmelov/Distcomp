using System;
using Application.Abstractions;
using Domain.Mappers;
using Domain.Models;
using Domain.Projections;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Marker.Commands;

public class UpdateMarkerCommandHandler : ICommandHandler<UpdateMarkerCommand, MarkerProjection>
{
    private readonly IMarkerRepository _markerRepository;

    public UpdateMarkerCommandHandler(IMarkerRepository markerRepository)
    {
        _markerRepository = markerRepository;
    }

    public async Task<Result<MarkerProjection>> Handle(UpdateMarkerCommand request, CancellationToken cancellationToken)
    {
        var resultMarker = await _markerRepository.UpdateMarker(new MarkerModel{
            Id = request.id,
            Name = request.name
        });

        if(!resultMarker.IsSuccess)
        {
            return Result.Failure<MarkerProjection>(resultMarker.Error);
        }
        
        return resultMarker.Value.ToMarkerProjection();
    }
}
