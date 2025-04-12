using distcomp.DTOs;
using distcomp.Services;
using Microsoft.AspNetCore.Mvc;

namespace distcomp.Controllers
{
    [ApiController]
    [Route("api/v1.0/notes")]
    public class NoteController: ControllerBase
    {
        private readonly NoteService _noteService;

        public NoteController(NoteService noteService)
        {
            _noteService = noteService;
        }

        

        [HttpPost]
        public IActionResult CreateNote([FromBody] NoteRequestTo noteRequest)
        {
            var noteResponse = _noteService.CreateNote(noteRequest);
            return CreatedAtAction(nameof(GetNote), new { id = noteResponse.Id }, noteResponse);
        }

      
        [HttpGet("{id}")]
        public IActionResult GetNote(long id)
        {
            var noteResponse = _noteService.GetNoteById(id);
            return noteResponse != null ? Ok(noteResponse) : NotFound();
        }

        
        [HttpGet]
        public IActionResult GetAllNotes()
        {
            var noteResponses = _noteService.GetAllNotes();
            return Ok(noteResponses);
        }


        
        [HttpPut]
        public IActionResult UpdateNote(long id, [FromBody] NoteRequestTo noteRequest)
        {
            //var postResponse = _postService.UpdatePost(id, postRequest);
            

            if (noteRequest == null || noteRequest.Id <= 0)
            {
                return BadRequest("Invalid note data.");
            }

            ///!!!
            var updatedNote = _noteService.UpdateNote(noteRequest.Id, noteRequest);
            if (updatedNote == null)
            {
                return NotFound();
            }

            return Ok(updatedNote);
        }

        
        [HttpDelete("{id}")]
        public IActionResult DeleteNote(long id)
        {
            var isDeleted = _noteService.DeleteNote(id);
            return isDeleted ? NoContent() : NotFound();
        }
    }
}
