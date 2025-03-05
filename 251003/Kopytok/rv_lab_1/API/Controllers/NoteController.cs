using Application.abstractions;
using Application.Services;
using Core;
using DTO.requests;
using DTO.responces;
using Microsoft.AspNetCore.Mvc;

// For more information on enabling Web API for empty projects, visit https://go.microsoft.com/fwlink/?LinkID=397860

namespace rv_lab_1.controllers
{
    [Route("api/v1.0/notes")]
    [ApiController]
    public class NoteController(INoteService noteService) : ControllerBase
    {
        private readonly INoteService _noteService = noteService;

        [HttpGet("{id:long}")]
        public async Task<NoteResponseTo> GetByIdAsync(long id)
        {
            return await _noteService.GetByIdAsync(id);
        }

        [HttpGet]
        public async Task<IEnumerable<NoteResponseTo>> GetAllAsync()
        {
            return await _noteService.GetAllAsync();
        }

        [HttpPost]
        public async Task<ActionResult<NoteResponseTo>> PostAsync([FromBody] NoteRequestTo requestTo)
        {
            var res = await _noteService.CreateAsync(requestTo);
            if (res == null)
                return BadRequest();
            return Created(string.Empty, res);
        }

        [HttpPut]
        public async Task<ActionResult<NoteRequestTo>> Put([FromBody] Note requestTo)
        {
            var res = await _noteService.UpdateAsync(requestTo.Id, new NoteRequestTo 
                { Content = requestTo.Content, StoryId = requestTo.StoryId});
            return Ok(res);
        }

        [HttpDelete("{id}")]
        public async Task<ActionResult> Delete(int id)
        {
            var res = await _noteService.DeleteAsync(id);
            if (!res)
                return BadRequest();
            return NoContent();
        }
    }
}
