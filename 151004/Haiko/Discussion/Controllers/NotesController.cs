using Discussion.Services.DataProviderServices;
using Discussion.Views.DTO;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;

namespace Discussion.Controllers
{
    [Route("api/v1.0/notes")]
    [ApiController]
    //[Authorize]
    public class NotesController : ControllerBase
    {
        private readonly IDataProvider _context;    

        public NotesController(IDataProvider context)
        {
            _context = context;
        }

        [HttpGet]
        public IActionResult GetNotes()
        {
            var res = _context.GetNotes();
            return StatusCode(200, res);
        }

        [HttpGet("{id}")]
        public IActionResult GetNote(int id)
        {
            var res = _context.GetNote(id);
            if (res == null)
                return StatusCode(400);
            return StatusCode(200, res);
        }

        [HttpPut]
        public IActionResult UpdateNote([FromBody] NoteUpdateDTO note)
        {
            var res = _context.UpdateNote(note);
            return StatusCode(200, res);
        }

        [HttpPost]
        public IActionResult PostNote([FromBody] NoteAddDTO note)
        {
            var res = _context.CreateNote(note);
            return StatusCode(201, res);
        }

        [HttpDelete("{id}")]
        public IActionResult DeleteNote(int id)
        {
            var res = _context.DeleteNote(id);
            if (res == 1)
                return StatusCode(204);
            else
                return StatusCode(400);
        }
    }
}
