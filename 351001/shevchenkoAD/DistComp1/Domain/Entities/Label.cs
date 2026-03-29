using Domain.Abstractions;

namespace Domain.Entities;

public class Label : BaseEntity {
    public string Name { get; set; } = "";
}