using AutoMapper;
using DistComp.Application.DTOs.Requests;
using DistComp.Application.DTOs.Responses;
using DistComp.Application.Exceptions;
using DistComp.Application.Services.Abstractions;
using DistComp.Application.Services.Interfaces;
using DistComp.Domain.Entities;
using DistComp.Domain.Interfaces;

namespace DistComp.Application.Services;

public class AuthorService : BaseService<Author, AuthorRequestTo, AuthorResponseTo>, IAuthorService {
    public AuthorService(IRepository<Author> repository,
                         IMapper mapper)
        : base(repository, mapper) {
    }

    protected override int NotFoundSubCode {
        get { return 15; }
    }

    protected override string EntityName {
        get { return "Author"; }
    }

    protected override void ValidateRequest(AuthorRequestTo req) {
        if (string.IsNullOrWhiteSpace(req.Login) || req.Login.Length < 2 || req.Login.Length > 64)
            throw new RestException(400, 11, "Login must be between 2 and 64 characters");

        if (string.IsNullOrWhiteSpace(req.Password) || req.Password.Length < 8 || req.Password.Length > 128)
            throw new RestException(400, 12, "Password must be between 8 and 128 characters");

        if (string.IsNullOrWhiteSpace(req.Firstname) || req.Firstname.Length < 2 || req.Firstname.Length > 64)
            throw new RestException(400, 13, "Firstname must be between 2 and 64 characters");

        if (string.IsNullOrWhiteSpace(req.Lastname) || req.Lastname.Length < 2 || req.Lastname.Length > 64)
            throw new RestException(400, 14, "Lastname must be between 2 and 64 characters");
    }
}