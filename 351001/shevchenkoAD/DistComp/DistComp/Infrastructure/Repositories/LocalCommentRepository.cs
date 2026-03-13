using DistComp.Domain.Entities;
using DistComp.Infrastructure.Abstractions;

namespace DistComp.Infrastructure.Repositories;

public class LocalCommentRepository : LocalBaseRepository<Comment> {
    protected override Comment Copy(Comment src) {
        return new Comment {
            Id = src.Id,
            IssueId = src.IssueId,
            Content = src.Content
        };
    }
}