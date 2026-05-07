using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Publisher.Application.DTOs.Requests;
using Publisher.Application.Services.Interfaces;

namespace Publisher.Presentation.Controllers.v2;

[ApiController]
[Route("api/v2.0/labels")]
[Authorize]
public class LabelsControllerV2 : ControllerBase
{
    private readonly ILabelService _service;

    public LabelsControllerV2(ILabelService service)
    {
        _service = service;
    }

    [HttpGet]
    [AllowAnonymous]
    public async Task<IActionResult> GetAll()
    {
        return Ok(await _service.GetAllAsync());
    }

    [HttpPost]
    [Authorize(Roles = "ADMIN")]
    public async Task<IActionResult> Create([FromBody] LabelRequestTo req)
    {
        return StatusCode(201, await _service.CreateAsync(req));
    }

    [HttpDelete("{id:long}")]
    [Authorize(Roles = "ADMIN")]
    public async Task<IActionResult> Delete(long id)
    {
        await _service.DeleteAsync(id);
        return NoContent();
    }
}