using MediatR;
using Microsoft.AspNetCore.Mvc;
using RW.Application.DTOs.Request;
using RW.Application.Features.Authors.Commands;
using RW.Application.Features.Authors.Queries;

namespace RW.Api.Controllers;

[ApiController]
[Route("api/v1.0/authors")]
public class AuthorsController : ControllerBase
{
    private readonly ISender _sender;

    public AuthorsController(ISender sender)
    {
        _sender = sender;
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

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] AuthorRequestTo dto)
    {
        var result = await _sender.Send(new CreateAuthorCommand(dto));
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] AuthorRequestTo dto)
    {
        var result = await _sender.Send(new UpdateAuthorCommand(dto.Id, dto));
        return Ok(result);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _sender.Send(new DeleteAuthorCommand(id));
        return NoContent();
    }
}
