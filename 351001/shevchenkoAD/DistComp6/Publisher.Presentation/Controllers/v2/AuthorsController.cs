using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.DTOs.Responses;
using Publisher.Application.Services.Interfaces;
using Shared.DTOs.Requests;
using Shared.DTOs.Responses;

namespace Publisher.Presentation.Controllers.v2;

[ApiController]
[Route("api/v2.0/authors")]
[Authorize]
public class AuthorsControllerV2 : ControllerBase
{
    private readonly IAuthorService _authorService;
    private readonly IAuthService _authService;

    public AuthorsControllerV2(IAuthorService authorService, IAuthService authService)
    {
        _authorService = authorService;
        _authService = authService;
    }


    [HttpPost("/api/v2.0/login")]
    [AllowAnonymous]
    public async Task<ActionResult<AuthResponseTo>> Login([FromBody] LoginRequestTo request)
    {
        var response = await _authService.LoginAsync(request);
        return Ok(response);
    }


    [HttpPost]
    [AllowAnonymous]
    public async Task<ActionResult<AuthorResponseTo>> Register([FromBody] AuthorRequestTo request)
    {
        var response = await _authService.RegisterAsync(request);
        return StatusCode(201, response);
    }


    [HttpGet("{id:long}")]
    public async Task<ActionResult<AuthorResponseTo>> GetById(long id)
    {
        var result = await _authorService.GetByIdAsync(id);
        return Ok(result);
    }


    [HttpGet]
    public async Task<ActionResult<IEnumerable<AuthorResponseTo>>> GetAll()
    {
        var result = await _authorService.GetAllAsync();
        return Ok(result);
    }


    [HttpPut]
    public async Task<ActionResult<AuthorResponseTo>> Update([FromBody] AuthorRequestTo request)
    {
        var result = await _authorService.UpdateAsync(request);
        return Ok(result);
    }


    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _authorService.DeleteAsync(id);
        return NoContent();
    }
}