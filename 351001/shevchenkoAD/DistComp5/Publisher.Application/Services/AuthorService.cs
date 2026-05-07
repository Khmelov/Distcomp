using AutoMapper;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Exceptions;
using Publisher.Application.Services.Abstractions;
using Publisher.Application.Services.Interfaces;
using Publisher.Domain.Entities;
using Publisher.Domain.Interfaces;
using Shared.Interfaces;

namespace Publisher.Application.Services;

public class AuthorService : BaseService<Author, AuthorRequestTo, AuthorResponseTo>, IAuthorService
{
    public AuthorService(IRepository<Author> repository,
        IMapper mapper,
        ICacheService cache)
        : base(repository, mapper, cache)
    {
    }

    protected override int NotFoundSubCode => 15;

    protected override string EntityName => "Author";

    public override async Task<AuthorResponseTo> CreateAsync(AuthorRequestTo request)
    {
        ValidateRequest(request);

        var exists = await _repository.ExistsAsync(a => a.Login == request.Login);
        if (exists) throw new RestException(403, 16, $"Author with login '{request.Login}' already exists");

        return await base.CreateAsync(request);
    }

    protected override void ValidateRequest(AuthorRequestTo req)
    {
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