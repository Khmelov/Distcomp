using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Services.Abstractions;
using Application.Services.Interfaces;
using AutoMapper;
using Domain.Entities;
using Domain.Interfaces;

namespace Application.Services;

public class AuthorService : BaseService<Author, AuthorRequestTo, AuthorResponseTo>, IAuthorService {
    public AuthorService(IRepository<Author> repository,
                         IMapper mapper)
        : base(repository, mapper) {
    }
    
    public override async Task<AuthorResponseTo> CreateAsync(AuthorRequestTo request) {
        ValidateRequest(request);
        
        bool exists = await _repository.ExistsAsync(a => a.Login == request.Login);
        if (exists)
        {
            throw new RestException(403, 16, $"Author with login '{request.Login}' already exists");
        }
        
        return await base.CreateAsync(request);
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