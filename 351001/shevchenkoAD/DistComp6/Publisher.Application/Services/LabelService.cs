using System.IdentityModel.Tokens.Jwt;
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

namespace Publisher.Application.Services;

public class LabelService : BaseService<Label, LabelRequestTo, LabelResponseTo>, ILabelService
{
    private readonly IHttpContextAccessor _httpContextAccessor;

    public LabelService(
        IRepository<Label> repository,
        IMapper mapper,
        ICacheService cache,
        IHttpContextAccessor httpContextAccessor)
        : base(repository, mapper, cache)
    {
        _httpContextAccessor = httpContextAccessor;
    }

    protected override int NotFoundSubCode => 35;
    protected override string EntityName => "Label";


    public override async Task<IEnumerable<LabelResponseTo>> GetAllAsync()
    {
        return await base.GetAllAsync();
    }

    public override async Task<LabelResponseTo> GetByIdAsync(long id)
    {
        return await base.GetByIdAsync(id);
    }


    public override async Task<LabelResponseTo> CreateAsync(LabelRequestTo request)
    {
        var (login, role) = GetCurrentUser();


        if (!string.IsNullOrEmpty(login) && role != "ADMIN")
            throw new RestException(403, 31, "Access denied: Only administrators can create labels");

        return await base.CreateAsync(request);
    }


    public override async Task<LabelResponseTo> UpdateAsync(LabelRequestTo request)
    {
        var (login, role) = GetCurrentUser();

        if (!string.IsNullOrEmpty(login) && role != "ADMIN")
            throw new RestException(403, 32, "Access denied: Only administrators can update labels");

        return await base.UpdateAsync(request);
    }


    public override async Task<bool> DeleteAsync(long id)
    {
        var (login, role) = GetCurrentUser();

        if (!string.IsNullOrEmpty(login) && role != "ADMIN")
            throw new RestException(403, 33, "Access denied: Only administrators can delete labels");

        return await base.DeleteAsync(id);
    }


    private (string Login, string Role) GetCurrentUser()
    {
        var user = _httpContextAccessor.HttpContext?.User;
        var login = user?.FindFirst(JwtRegisteredClaimNames.Sub)?.Value ?? string.Empty;
        var role = user?.FindFirst(ClaimTypes.Role)?.Value ?? string.Empty;
        return (login, role);
    }


    protected override void ValidateRequest(LabelRequestTo req)
    {
        if (string.IsNullOrWhiteSpace(req.Name) || req.Name.Length < 2 || req.Name.Length > 32)
            throw new RestException(400, 31, "Name must be between 2 and 32 characters");
    }
}