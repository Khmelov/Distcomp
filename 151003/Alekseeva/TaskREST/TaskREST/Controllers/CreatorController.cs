using Asp.Versioning;
using Microsoft.AspNetCore.Mvc;
using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;
using TaskREST.Models;
using TaskREST.Services.Interfaces;

namespace TaskREST.Controllers;

[Route("api/v{version:apiVersion}/creators")]
[ApiVersion("1.0")]
[ApiController]
public class CreatorController(ICreatorService service) : ControllerBase
{
    [HttpGet("{id:long}")]
    public async Task<ActionResult<Creator>> GetCreator(long id)
    {
        return Ok(await service.GetCreatorById(id));
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<Creator>>> GetCreators()
    {
        return Ok(await service.GetCreators());
    }

    [HttpPost]
    public async Task<ActionResult<CreatorResponseTo>> CreateCreator(CreateCreatorRequestTo createCreatorRequestTo)
    {
        var addedCreator = await service.CreateCreator(createCreatorRequestTo);
        return CreatedAtAction(nameof(GetCreator), new { id = addedCreator.Id }, addedCreator);
    }

    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeleteCreator(long id)
    {
        await service.DeleteCreator(id);
        return NoContent();
    }

    [HttpPut]
    public async Task<ActionResult<CreatorResponseTo>> UpdateCreator(UpdateCreatorRequestTo updateCreatorRequestTo)
    {
        return Ok(await service.UpdateCreator(updateCreatorRequestTo));
    }
}