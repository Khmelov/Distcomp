using DistComp.Domain.Abstractions;

namespace DistComp.Domain.Entities;

public class Label : BaseEntity {
    public string Name { get; set; } = "";
}