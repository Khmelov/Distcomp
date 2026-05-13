using System.Security.Claims;
using FluentValidation;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace lab_1.Controllers;

[ApiController]
[Route("api/v2.0/authors")]
[Authorize]
public class AuthorsV2Controller : ControllerBase
{
    private readonly IBaseService<AuthorRequestDto, AuthorResponseDto> _authorService;
    private readonly IValidator<AuthorRequestDto> _authorValidator;
    private readonly AuthService _authService;

    public AuthorsV2Controller(
        IBaseService<AuthorRequestDto, AuthorResponseDto> authorService,
        IValidator<AuthorRequestDto> authorValidator,
        AuthService authService)
    {
        _authorService = authorService;
        _authorValidator = authorValidator;
        _authService = authService;
    }

    [HttpPost]
    [AllowAnonymous]
    public async Task<ActionResult<AuthorResponseDto>> Register([FromBody] AuthorRequestDto dto)
    {
        var validation = _authorValidator.Validate(dto);
        if (!validation.IsValid)
        {
            return BadRequest(new ErrorResponseDto
            {
                ErrorMessage = validation.Errors.First().ErrorMessage,
                ErrorCode = "40001"
            });
        }

        try
        {
            var entity = await _authService.RegisterAsync(dto);
            return CreatedAtAction(nameof(GetAuthor), new { id = entity.Id }, new AuthorResponseDto
            {
                Id = entity.Id,
                Login = entity.Login,
                Firstname = entity.Firstname,
                Lastname = entity.Lastname,
                Role = entity.Role
            });
        }
        catch (InvalidOperationException ex)
        {
            return Conflict(new ErrorResponseDto
            {
                ErrorMessage = ex.Message,
                ErrorCode = "40901"
            });
        }
    }

    [HttpGet]
    [Authorize(Roles = "ADMIN")]
    public ActionResult<IEnumerable<AuthorResponseDto>> GetAuthors() => Ok(_authorService.GetAll());

    [HttpGet("{id:long}")]
    public ActionResult<AuthorResponseDto> GetAuthor(long id)
    {
        var author = _authorService.Read(id);
        if (author == null)
        {
            return NotFound(new ErrorResponseDto
            {
                ErrorMessage = "Author not found.",
                ErrorCode = "40401"
            });
        }

        var currentRole = User.FindFirstValue(ClaimTypes.Role);
        var currentLogin = User.FindFirstValue(ClaimTypes.Name);
        if (!string.Equals(currentRole, "ADMIN", StringComparison.OrdinalIgnoreCase) &&
            !string.Equals(author.Login, currentLogin, StringComparison.Ordinal))
        {
            return Forbid();
        }

        return Ok(author);
    }
}
