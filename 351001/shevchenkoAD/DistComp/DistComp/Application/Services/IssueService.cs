using AutoMapper;
using DistComp.Application.DTOs.Requests;
using DistComp.Application.DTOs.Responses;
using DistComp.Application.Exceptions;
using DistComp.Application.Services.Abstractions;
using DistComp.Application.Services.Interfaces;
using DistComp.Domain.Entities;
using DistComp.Domain.Interfaces;

namespace DistComp.Application.Services;

public class IssueService : BaseService<Issue, IssueRequestTo, IssueResponseTo>, IIssueService {
    public IssueService(IRepository<Issue> repository,
                        IMapper mapper)
        : base(repository, mapper) {
    }

    protected override int NotFoundSubCode {
        get { return 25; }
    }

    protected override string EntityName {
        get { return "Issue"; }
    }

    protected override void ValidateRequest(IssueRequestTo req) {
        if (string.IsNullOrWhiteSpace(req.Title) || req.Title.Length < 2 || req.Title.Length > 64)
            throw new RestException(400, 21, "Title must be between 2 and 64 characters");

        if (string.IsNullOrWhiteSpace(req.Content) || req.Content.Length < 4 || req.Content.Length > 2048)
            throw new RestException(400, 22, "Content must be between 4 and 2048 characters");
    }

    protected override void BeforeCreate(Issue entity) {
        entity.Created = DateTime.UtcNow;
        entity.Modified = DateTime.UtcNow;
    }

    protected override void BeforeUpdate(Issue entity) {
        entity.Modified = DateTime.UtcNow;
    }
}