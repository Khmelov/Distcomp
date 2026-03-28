using Domain.Entities;
using Infrastructure.Abstractions;

namespace Infrastructure.Repositories;

public class LocalCommentRepository : LocalBaseRepository<Comment> {
    protected override Comment Copy(Comment src) {
        return new Comment {
            Id = src.Id,
            IssueId = src.IssueId,
            Content = src.Content
        };
    }
}