using DistComp.Domain.Abstractions;

namespace DistComp.Domain.Entities;

public class Issue : BaseEntity {
    public long AuthorId { get; set; }
    public string Title { get; set; } = "";
    public string Content { get; set; } = "";
    public DateTime Created { get; set; }
    public DateTime Modified { get; set; }
    public List<long> LabelIds { get; set; } = new();
}