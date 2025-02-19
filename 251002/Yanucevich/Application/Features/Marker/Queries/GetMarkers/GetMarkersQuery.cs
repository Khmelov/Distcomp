using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.Marker.Queries;

public record GetMarkersQuery() : IQuery<IEnumerable<MarkerProjection>>;
