using MediatR;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RW.Application.DTOs.Request;
using RW.Application.Features.Tags.Commands;
using RW.Application.Features.Tags.Queries;

namespace RW.Api.Controllers.V2;

[ApiController]
[Route("api/v2.0/tags")]
[Authorize(AuthenticationSchemes = JwtBearerDefaults.AuthenticationScheme)]
public class TagsV2Controller : ControllerBase
{
    private readonly ISender _sender;

    public TagsV2Controller(ISender sender)
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
    [Authorize(Policy = "AdminOnly")]
    public async Task<IActionResult> Create([FromBody] TagRequestTo dto)
    {
        var result = await _sender.Send(new CreateTagCommand(dto));
        return CreatedAtAction(nameof(GetById), new { id = result.Id }, result);
    }

    [HttpPut]
    [Authorize(Policy = "AdminOnly")]
    public async Task<IActionResult> Update([FromBody] TagRequestTo dto)
    {
        var result = await _sender.Send(new UpdateTagCommand(dto.Id, dto));
        return Ok(result);
    }

    [HttpDelete("{id:long}")]
    [Authorize(Policy = "AdminOnly")]
    public async Task<IActionResult> Delete(long id)
    {
        await _sender.Send(new DeleteTagCommand(id));
        return NoContent();
    }
}
