using Domain.Entities;
using Infrastructure.Abstractions;

namespace Infrastructure.Repositories;

public class LocalLabelRepository : LocalBaseRepository<Label> {
    protected override Label Copy(Label src) {
        return new Label {
            Id = src.Id,
            Name = src.Name
        };
    }
}