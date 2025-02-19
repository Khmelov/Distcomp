using System;

namespace Domain.Projections;

public record MarkerProjection
{
    public long Id { get; init; }
    public string Name { get; init; }
}
