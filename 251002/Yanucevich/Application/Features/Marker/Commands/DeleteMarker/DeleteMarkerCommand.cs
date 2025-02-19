using Application.Abstractions;

namespace Application.Features.Marker.Commands;

public record DeleteMarkerCommand(long id) : ICommand;