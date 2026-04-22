using MediatR;
using Microsoft.AspNetCore.Mvc;
using RW.Application.DTOs.Request;
using RW.Application.Features.Tags.Commands;
using RW.Application.Features.Tags.Queries;

namespace RW.Api.Controllers;

[ApiController]
[Route("api/v1.0/tags")]
public class TagsController : ControllerBase
{
    private readonly ISender _sender;

    public TagsController(ISender sender)
    {
        _sender = sender;
    }

    [HttpGet]
    public async Task<IActionResult> GetAll()
    {
        var result = await _sender.Send(new GetTagsQuery());
        return Ok(result);
    }

    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetById(long id)
    {
        var result = await _sender.Send(new GetTagByIdQuery(id));
        return Ok(result);
    }

    [HttpPost]
    public async Task<IActionResult> Create([FromBody] TagRequestTo dto)
    {
        var result = await _sender.Send(new CreateTagCommand(dto));
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    public async Task<IActionResult> Update([FromBody] TagRequestTo dto)
    {
        var result = await _sender.Send(new UpdateTagCommand(dto.Id, dto));
        return Ok(result);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> Delete(long id)
    {
        await _sender.Send(new DeleteTagCommand(id));
        return NoContent();
    }
}
