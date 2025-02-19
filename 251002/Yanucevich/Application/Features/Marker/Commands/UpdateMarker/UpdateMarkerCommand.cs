using System.Windows.Input;
using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.Marker.Commands;
public record UpdateMarkerCommand(
    long id, 
    string name
): ICommand<MarkerProjection>;