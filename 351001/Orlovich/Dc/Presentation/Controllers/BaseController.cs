using Application.Service;
using Domain.Models;
using Microsoft.AspNetCore.Http.HttpResults;
using Microsoft.AspNetCore.Mvc;

namespace Dc.Controllers;

public abstract class BaseController<TResponseTo,TRequestTo>: ControllerBase
    where TResponseTo : class,IIdEntity
    where TRequestTo : class,IIdNullEntity
{
    private readonly IService<TResponseTo,TRequestTo> _service;

    public BaseController(IService<TResponseTo,TRequestTo> service)
    {
        _service = service;
    }

    [HttpGet]
    public virtual async Task<IEnumerable<TResponseTo>> Get()
    {
        return await _service.GetAllEditorsAsync();
    }
    
    [HttpGet("{id}")]
    public virtual async Task<TResponseTo> GetById([FromRoute] long id)
    {
        return await _service.GetEditorByIdAsync(id);
    }
    
    
    [HttpPost]
    public virtual async Task<IActionResult> Post([FromBody] TRequestTo editor)
    {
        var res = await _service.AddEditorAsync(editor);
        return Created("",res);
    }
    
    
        
    [HttpDelete("{id}")]
    public virtual async Task<IActionResult> Delete([FromRoute] long id)
    {
        var res = await _service.DeleteEditorAsync(id);
        
        if(res == 0)
            return NotFound();
        
        return NoContent();
    }
    
    
    [HttpPut]
    public virtual async Task<IActionResult> Update([FromBody] TRequestTo editor)
    {
        long id = (long)editor.id!;
        var res = await _service.UpdateEditorAsync(id, editor);
        
        if(res == null)
            return NotFound();
        
        return Ok(res);
    }   
}