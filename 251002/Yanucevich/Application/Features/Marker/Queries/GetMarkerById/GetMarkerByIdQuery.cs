using Application.Abstractions;
using Domain.Projections;

namespace Application.Features.Marker.Queries;

public record GetMarkerByIdQuery(long id): IQuery<MarkerProjection>;