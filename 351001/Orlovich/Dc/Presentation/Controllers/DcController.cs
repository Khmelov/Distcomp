using Application.Repository;
using Application.Service;
using Domain.Models;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

[ApiController]
[Route("api/v1.0")]
public class DcController : ControllerBase
{
    private readonly ILogger<DcController> _logger;
    private readonly IService _service;

    public DcController(ILogger<DcController> logger,IService service)
    {
        _logger = logger;
        _service = service;
    }

    [HttpGet("authors")]
    public async Task<IEnumerable<EditorResponseTo>> Get()
    {
        return await _service.GetAllEditorsAsync();
    }
    
    [HttpGet("authors/{id}")]
    public async Task<EditorResponseTo> GetById([FromRoute] long id)
    {
        return await _service.GetEditorByIdAsync(id);
    }
    
    
    [HttpPost("authors")]
    public async Task<IActionResult> Post([FromBody] EditorRequestTo editor)
    {
        var res = await _service.AddEditorAsync(editor);
        return Created("",res);
    }
    
    
        
    [HttpDelete("authors/{id}")]
    public async Task<IActionResult> Delete([FromRoute] long id)
    {
        var res = await _service.DeleteEditorAsync(id);
        
        if(res == 0)
            return NotFound();
        
        return NoContent();
    }
    
    
    [HttpPut("authors")]
    public async Task<IActionResult> Update([FromBody] EditorRequestTo editor)
    {
        long id = (long)editor.id!;
        var res = await _service.UpdateEditorAsync(id, editor);
        
        if(res == null)
            return NotFound();
        
        return Ok(res);
    }
}