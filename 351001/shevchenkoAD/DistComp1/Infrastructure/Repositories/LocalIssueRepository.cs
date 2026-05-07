using Domain.Entities;
using Infrastructure.Abstractions;

namespace Infrastructure.Repositories;

public class LocalIssueRepository : LocalBaseRepository<Issue> {
    protected override Issue Copy(Issue src) {
        return new Issue {
            Id = src.Id,
            AuthorId = src.AuthorId,
            Title = src.Title,
            Content = src.Content,
            Created = src.Created,
            Modified = src.Modified,
            LabelIds = src.LabelIds != null ? new List<long>(src.LabelIds) : new List<long>()
        };
    }
}