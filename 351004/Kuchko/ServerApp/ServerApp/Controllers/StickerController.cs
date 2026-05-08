using Microsoft.AspNetCore.Mvc;
using ServerApp.Models.DTOs.Requests;
using ServerApp.Models.DTOs.Responses;
using ServerApp.Services.Interfaces;

namespace ServerApp.Controllers;

[ApiController]
[Route("stickers")]
public class StickerController(IStickerService stickerService) : ControllerBase
{
    [HttpGet]
    public ActionResult<IEnumerable<StickerResponseTo>> GetAll() => Ok(stickerService.GetAll());

    [HttpGet("{id:long}")]
    public ActionResult<StickerResponseTo> GetById(long id) => Ok(stickerService.GetById(id));

    [HttpPost]
    public ActionResult<StickerResponseTo> Create([FromBody] StickerRequestTo request)
    {
        var response = stickerService.Create(request);
        return CreatedAtAction(nameof(GetById), new { id = response.Id }, response);
    }

    [HttpPut("{id:long}")]
    public ActionResult<StickerResponseTo> Update(long id, [FromBody] StickerRequestTo request)
    {
        return Ok(stickerService.Update(id, request));
    }

    [HttpDelete("{id:long}")]
    public IActionResult Delete(long id)
    {
        stickerService.Delete(id);
        return NoContent();
    }
}