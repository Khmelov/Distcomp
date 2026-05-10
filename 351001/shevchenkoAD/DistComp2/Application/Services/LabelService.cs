using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Services.Abstractions;
using Application.Services.Interfaces;
using AutoMapper;
using Domain.Entities;
using Domain.Interfaces;

namespace Application.Services;

public class LabelService : BaseService<Label, LabelRequestTo, LabelResponseTo>, ILabelService {
    public LabelService(IRepository<Label> repository,
                        IMapper mapper)
        : base(repository, mapper) {
    }

    protected override int NotFoundSubCode {
        get { return 35; }
    }

    protected override string EntityName {
        get { return "Label"; }
    }

    protected override void ValidateRequest(LabelRequestTo req) {
        if (string.IsNullOrWhiteSpace(req.Name) || req.Name.Length < 2 || req.Name.Length > 32)
            throw new RestException(400, 31, "Name must be between 2 and 32 characters");
    }
}