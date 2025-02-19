using System;
using Application.Abstractions;
using Domain.Repositories;
using Domain.Shared;

namespace Application.Features.Marker.Commands;

public class DeleteMarkerCommandHandler : ICommandHandler<DeleteMarkerCommand>
{
    private readonly IMarkerRepository _markerRepository;

    public DeleteMarkerCommandHandler(IMarkerRepository markerRepository)
    {
        _markerRepository = markerRepository;
    }

    public async Task<Result> Handle(DeleteMarkerCommand request, CancellationToken cancellationToken)
    {
        var resultDelete = await _markerRepository.DeleteMarker(request.id);

        if(!resultDelete.IsSuccess)
        {
            return Result.Failure(resultDelete.Error);
        }

        return Result.Success();
    }
}
