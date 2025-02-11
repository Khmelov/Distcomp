using Application.DTO.Request.Mark;
using Application.DTO.Response.Mark;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace WebApi.Controllers;

[ApiController]
[Route("/api/v1.0/marks")]
public class MarkController  (IMarkService _markService) : ControllerBase
{
    [HttpGet]
    public async Task<IActionResult> GetAllMark()
    {
        var mark = await _markService.GetMarks(new MarkRequestToGetAll());
        return Ok(mark);
    }
    
    [HttpGet("{id:long}")]
    public async Task<IActionResult> GetMarkById(long id)
    {
        MarkResponseToGetById mark = await _markService.GetMarkById(new MarkRequestToGetById() {Id = id});
        return Ok(mark);
    }

    [HttpPost]
    public async Task<IActionResult> CreateMark(MarkRequestToCreate markRequestToCreate)
    {
        var mark = await _markService.CreateMark(markRequestToCreate);
        return CreatedAtAction(nameof(GetMarkById), new { id = mark.Id }, mark);
    }
    
    [HttpPut]
    public async Task<IActionResult> UpdateMark(MarkRequestToFullUpdate markModel)
    {
        var mark = await _markService.UpdateMark(markModel);
        return Ok(mark);
    }
    
    [HttpDelete("{id:long}")]
    public async Task<IActionResult> DeleteMark(long id)
    {
        var mark = await _markService.DeleteMark(new MarkRequestToDeleteById(){Id = id});
        return NoContent();
    }
}