using Application.DTO.Request;
using Application.Features.Labels.Commands;
using Application.Features.Labels.Queries;
using MediatR;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers;

[ApiController]
[Route("api/v1.0/labels")]
public class LabelController : ControllerBase
{
    private readonly ISender _sender;

    public LabelController(ISender sender)
    {
        _sender = sender;
    }

    [HttpGet]
    public async Task<IActionResult> GetLabels()
    {
        var labelsResponseTo = await _sender.Send(new ReadLabelsQuery());

        return Ok(labelsResponseTo);
    }

    [HttpGet("{id:long}", Name = "GetLabelById")]
    public async Task<IActionResult> GetLabelById([FromRoute] long id)
    {
        var labelResponseTo = await _sender.Send(new ReadLabelQuery(id));

        return Ok(labelResponseTo);
    }

    [HttpPost]
    public async Task<IActionResult> CreateLabel([FromBody] LabelRequestTo labelRequestTo)
    {
        var labelResponseTo = await _sender.Send(new CreateLabelCommand(labelRequestTo));

        return CreatedAtRoute(nameof(GetLabelById), new { id = labelResponseTo.Id }, labelResponseTo);
    }

    [HttpPut]
    public async Task<IActionResult> UpdateLabel([FromBody] UpdateLabelRequestTo updateLabelRequestTo)
    {
        var labelRequestTo = new LabelRequestTo(updateLabelRequestTo.Name);

        var labelResponseTo = await _sender.Send(new UpdateLabelCommand(updateLabelRequestTo.Id, labelRequestTo));

        return Ok(labelResponseTo);
    }

    [HttpPut("{id:long}")]
    public async Task<IActionResult> UpdateLabelById([FromRoute] long id, [FromBody] LabelRequestTo labelRequestTo)
    {
        var labelResponseTo = await _sender.Send(new UpdateLabelCommand(id, labelRequestTo));

        return Ok(labelResponseTo);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeleteLabel([FromRoute] long id)
    {
        await _sender.Send(new DeleteLabelCommand(id));

        return NoContent();
    }
}