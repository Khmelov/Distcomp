using Microsoft.AspNetCore.Mvc;
using RV.Services.DataProviderServices;
using RV.Views.DTO;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using Microsoft.Extensions.Caching.Distributed;
using Newtonsoft.Json;
using RV.Models;

namespace RV.Controllers
{
    [Route("api/v1.0/notes")]
    [ApiController]
    public class NotesController : ControllerBase
    {
        private readonly IDataProvider _context;
        private readonly IDistributedCache _cache;

        public NotesController(IDataProvider context, IDistributedCache cache)
        {
            _context = context;
            _cache = cache;
        }

        [HttpGet]
        public IActionResult GetNotes()
        {
            return StatusCode(200, _context.GetNotes());
        }

        [HttpGet("{id}")]
        public IActionResult GetNote(int id)
        {
            var res = _cache.GetString("note_" + id.ToString());
            if (res != null)
                return StatusCode(200, JsonConvert.DeserializeObject<NoteDTO>(res));
            return StatusCode(200, _context.GetNote(id));
        }

        [HttpPut]
        public IActionResult UpdateNote([FromBody] NoteUpdateDTO note)
        {
            try
            {
                var newNote = _context.UpdateNote(note);
                _cache.SetString("note_" + newNote.id.ToString(), JsonConvert.SerializeObject(newNote), new DistributedCacheEntryOptions
                {
                    AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(5)
                });
                return StatusCode(200, newNote);
            }
            catch (DbUpdateException ex)
            {
                string errorCode = "400";
                string hash = ex.Message.GetHashCode().ToString();
                errorCode += hash[0];
                errorCode += hash[1];
                Dictionary<string, string> error = new Dictionary<string, string>() {
                    { "errorMeassage", ex.Message},
                    { "errorCode", errorCode}
                };
                return StatusCode(400, error);
            }
        }

        [HttpPost]
        public IActionResult PostNote([FromBody] NoteAddDTO note)
        {
            try
            { 
                if(note.content.Length < 2)
                {
                    throw new DbUpdateException();
                }
                var t = _context.GetTweet(note.tweetId);
                if (t == null)
                {
                    throw new DbUpdateException();
                }
                return StatusCode(201, _context.CreateNote(note));
            }
            catch (DbUpdateException ex)
            {
                int statusCode = 400;
                if (ex.InnerException is PostgresException postgresException)
                {
                    if (postgresException.SqlState == "23505")
                    {
                        statusCode = 403;
                    }
                }
                string hash = ex.Message.GetHashCode().ToString();
                string errorCode = statusCode.ToString();
                errorCode += hash[0];
                errorCode += hash[1];
                Dictionary<string, string> error = new Dictionary<string, string>() {
                    { "errorMeassage", ex.Message},
                    { "errorCode", errorCode}
                };
                return StatusCode(statusCode, error);
            }
        }

        [HttpDelete("{id}")]
        public IActionResult DeleteNote(int id)
        {
            int res = _context.DeleteNote(id);
            if (res == 0)
                return StatusCode(400);
            else
            {
                _cache.Remove("note_" + id.ToString());
                return StatusCode(204);
            }
        }
    }
}
