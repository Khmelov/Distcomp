using MediatR;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using RW.Api.Security;
using RW.Application.DTOs.Request;
using RW.Application.DTOs.Response;
using RW.Application.Exceptions;
using RW.Application.Features.Authors.Commands;
using RW.Application.Features.Authors.Queries;
using RW.Domain.Entities;
using RW.Infrastructure.Data;

namespace RW.Api.Controllers.V2;

[ApiController]
[Route("api/v2.0/authors")]
[Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
public class AuthorsV2Controller : ControllerBase
{
    private readonly ISender _sender;
    private readonly ApplicationDbContext _db;

    public AuthorsV2Controller(ISender sender, ApplicationDbContext db)
    {
        _sender = sender;
        _db = db;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var result = await _sender.Send(new GetAuthorsQuery());
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetById(long id)
    {
        var result = await _sender.Send(new GetAuthorByIdQuery(id));
        return Ok(result);
    }

    [HttpGet("me")]
    public async Task<IActionResult> Me()
    {
        var login = User.Identity?.Name ?? string.Empty;
        var author = await _db.Authors.AsNoTracking().FirstOrDefaultAsync(a => a.Login == login);
        if (author is null)
            throw new NotFoundException("Author", 0);

        return Ok(new AuthorResponseTo
        {
            Id = author.Id,
            Login = author.Login,
            Password = author.Password,
            FirstName = author.FirstName,
            LastName = author.LastName,
            Role = author.Role
        });
    }

    [HttpPost]
    [AllowAnonymous]
    public async Task<IActionResult> Register([FromBody] AuthorRequestTo dto)
    {
        var result = await _sender.Send(new CreateAuthorCommand(dto, HashPassword: true));
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] AuthorRequestTo dto)
    {
        EnsureCanModifyAuthor(dto.Id);
        var result = await _sender.Send(new UpdateAuthorCommand(dto.Id, dto));
        return Ok(result);
    }

    [HttpDelete("{id:long}")]
    [Authorize(Policy = "AdminOnly")]
    public async Task<IActionResult> Delete(long id)
    {
        await _sender.Send(new DeleteAuthorCommand(id));
        return NoContent();
    }

    private void EnsureCanModifyAuthor(long targetId)
    {
        if (User.IsInRole("ADMIN")) return;

        var idClaim = User.FindFirst(System.Security.Claims.ClaimTypes.NameIdentifier)?.Value;
        if (long.TryParse(idClaim, out var currentId) && currentId == targetId) return;

        throw new ForbiddenException("You can modify only your own profile.");
    }
}
