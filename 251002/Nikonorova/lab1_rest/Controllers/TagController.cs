using distcomp.DTOs;
using distcomp.Services;
using Microsoft.AspNetCore.Mvc;

namespace distcomp.Controllers
{
    [ApiController]
    [Route("api/v1.0/tags")]
    public class TagController : ControllerBase
    {

        private readonly TagService _tagService;

        public TagController(TagService tagService)
        {
            _tagService = tagService;
        }

        
        [HttpPost]
        public IActionResult CreateTag([FromBody] TagRequestTo tagRequest)
        {
            var tagResponse = _tagService.CreateSticker(tagRequest);
            return CreatedAtAction(nameof(GetTag), new { id = tagResponse.Id }, tagResponse);
        }

        
        [HttpGet("{id}")]
        public IActionResult GetTag(long id)
        {
            var tagResponse = _tagService.GetTagById(id);
            return tagResponse != null ? Ok(tagResponse) : NotFound();
        }

        
        [HttpGet]
        public IActionResult GetAllTags()
        {
            var tagResponses = _tagService.GetAllTags();
            return Ok(tagResponses);
        }

        
        [HttpPut]
        public IActionResult UpdateTag(long id, [FromBody] TagRequestTo tagRequest)
        {
            // var stickerResponse = _stickerService.UpdateSticker(id, stickerRequest);
            // return stickerResponse != null ? Ok(stickerResponse) : NotFound();

            if (tagRequest == null || tagRequest.Id <= 0)
            {
                return BadRequest("Invalid sticker data.");
            }

            var updatedTag = _tagService.UpdateTag(tagRequest.Id, tagRequest);
            if (updatedTag == null)
            {
                return NotFound();
            }

            return Ok(updatedTag);
        }

        

        [HttpDelete("{id}")]
        public IActionResult DeleteTag(long id)
        {
            var isDeleted = _tagService.DeleteTag(id);
            return isDeleted ? NoContent() : NotFound();
        }
    }
}
