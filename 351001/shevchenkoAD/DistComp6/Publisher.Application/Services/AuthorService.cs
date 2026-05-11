using System.Security.Claims;
using AutoMapper;
using Microsoft.AspNetCore.Http;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Exceptions;
using Publisher.Application.Services.Abstractions;
using Publisher.Application.Services.Interfaces;
using Publisher.Domain.Entities;
using Publisher.Domain.Interfaces;
using Shared.Interfaces;
using BC = BCrypt.Net.BCrypt;
using JwtRegisteredClaimNames = System.IdentityModel.Tokens.Jwt.JwtRegisteredClaimNames;

namespace Publisher.Application.Services;

public class AuthorService : BaseService<Author, AuthorRequestTo, AuthorResponseTo>, IAuthorService
{
    private readonly IHttpContextAccessor _httpContextAccessor;

    public AuthorService(IRepository<Author> repository,
        IMapper mapper,
        ICacheService cache,
        IHttpContextAccessor httpContextAccessor)
        : base(repository, mapper, cache)
    {
        _httpContextAccessor = httpContextAccessor;
    }

    protected override int NotFoundSubCode => 15;
    protected override string EntityName => "Author";


    public override async Task<AuthorResponseTo> CreateAsync(AuthorRequestTo request)
    {
        ValidateRequest(request);


        var exists = await _repository.ExistsAsync(a => a.Login == request.Login);
        if (exists) throw new RestException(403, 16, $"Author with login '{request.Login}' already exists");

        var author = _mapper.Map<Author>(request);


        author.Password = BC.HashPassword(request.Password);

        BeforeCreate(author);
        var createdEntity = await _repository.CreateAsync(author);

        var response = _mapper.Map<AuthorResponseTo>(createdEntity);
        await _cache.SetAsync(GetCacheKey(response.Id ?? 0), response);

        return response;
    }


    public override async Task<AuthorResponseTo> GetByIdAsync(long id)
    {
        var authorDto = await base.GetByIdAsync(id);
        var (login, role) = GetCurrentUser();


        if (!string.IsNullOrEmpty(login) && role == "CUSTOMER")
            if (authorDto.Login != login)
                throw new RestException(403, 17, "Access denied: You can only view your own profile");

        return authorDto;
    }


    public override async Task<AuthorResponseTo> UpdateAsync(AuthorRequestTo request)
    {
        var id = request.Id ?? -1;
        var (login, role) = GetCurrentUser();


        var existingAuthor = await _repository.GetByIdAsync(id);
        if (existingAuthor == null) ThrowNotFound(id);


        if (!string.IsNullOrEmpty(login) && role == "CUSTOMER" && existingAuthor.Login != login)
            throw new RestException(403, 18, "Access denied: You can only update your own profile");

        ValidateRequest(request);


        _mapper.Map(request, existingAuthor);
        existingAuthor.Password = BC.HashPassword(request.Password);

        BeforeUpdate(existingAuthor);
        var updatedEntity = await _repository.UpdateAsync(existingAuthor);

        var response = _mapper.Map<AuthorResponseTo>(updatedEntity);
        await _cache.SetAsync(GetCacheKey(id), response);

        return response;
    }


    public override async Task<bool> DeleteAsync(long id)
    {
        var (login, role, currentUserId) = GetCurrentUserDelete();


        if (!string.IsNullOrEmpty(login) && role == "CUSTOMER")
            if (currentUserId != id.ToString())
                throw new RestException(403, 19, "Access denied");


        var existingAuthor = await _repository.GetByIdAsync(id);

        if (existingAuthor == null) ThrowNotFound(id);


        return await base.DeleteAsync(id);
    }


    private (string Login, string Role) GetCurrentUser()
    {
        var user = _httpContextAccessor.HttpContext?.User;
        var login = user?.FindFirst(JwtRegisteredClaimNames.Sub)?.Value ?? string.Empty;
        var role = user?.FindFirst(ClaimTypes.Role)?.Value ?? string.Empty;
        return (login, role);
    }

    private (string Login, string Role, string Id) GetCurrentUserDelete()
    {
        var user = _httpContextAccessor.HttpContext?.User;
        if (user == null) return ("", "", "");


        var login = user.FindFirst(ClaimTypes.Name)?.Value ?? "";


        var role = user.FindFirst(ClaimTypes.Role)?.Value ?? "";


        var id = user.FindFirst("userId")?.Value ?? "";

        return (login, role, id);
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