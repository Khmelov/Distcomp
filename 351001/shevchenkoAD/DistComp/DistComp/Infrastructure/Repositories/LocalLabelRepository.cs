using DistComp.Domain.Entities;
using DistComp.Infrastructure.Abstractions;

namespace DistComp.Infrastructure.Repositories;

public class LocalLabelRepository : LocalBaseRepository<Label> {
    protected override Label Copy(Label src) {
        return new Label {
            Id = src.Id,
            Name = src.Name
        };
    }
}