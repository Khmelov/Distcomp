using lab1.Models.DTO.Auth;
using lab1.Models.DTO.Responses;
using lab1.Repositories.Interfaces;
using lab1.Models.Entities;
using lab1.Security;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace lab1.Controllers;

[ApiController]
[Route("api/v2.0")]
public sealed class AuthController : ControllerBase
{
    private readonly IEntityRepository<Editor> _repository;
    private readonly JwtTokenService _tokenService;
    private readonly IPasswordHasher<Editor> _passwordHasher;

    public AuthController(
        IEntityRepository<Editor> repository,
        JwtTokenService tokenService,
        IPasswordHasher<Editor> passwordHasher)
    {
        _repository = repository;
        _tokenService = tokenService;
        _passwordHasher = passwordHasher;
    }

    [HttpPost("login")]
    public async Task<ActionResult<LoginResponse>> Login([FromBody] LoginRequest request, CancellationToken cancellationToken)
    {
        if (string.IsNullOrWhiteSpace(request.Login) || string.IsNullOrWhiteSpace(request.Password))
        {
            return Unauthorized(new ErrorResponse { ErrorCode = 40101, ErrorMessage = "Invalid login or password" });
        }

        var page = await _repository.GetPagedAsync(e => e.Login == request.Login, null, 0, 1, cancellationToken);
        var editor = page.Content.FirstOrDefault();
        if (editor is null)
        {
            return Unauthorized(new ErrorResponse { ErrorCode = 40101, ErrorMessage = "Invalid login or password" });
        }

        var verify = _passwordHasher.VerifyHashedPassword(editor, editor.Password, request.Password);
        if (verify == PasswordVerificationResult.Failed)
        {
            return Unauthorized(new ErrorResponse { ErrorCode = 40101, ErrorMessage = "Invalid login or password" });
        }

        return Ok(new LoginResponse { AccessToken = _tokenService.Generate(editor) });
    }
}
