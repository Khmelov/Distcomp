using Microsoft.AspNetCore.Mvc;
using WebApplication1.DTO;
using WebApplication1.Repository;
using WebApplication1.Service;

namespace WebApplication1.Controller
{
    [ApiController]
    [Route("api/v1.0/tags")]
    public class TagController : ControllerBase
    {
        private readonly ITagService _tagService;
        public TagController(ITagService tagService) => _tagService = tagService;

        [HttpGet]
        public async Task<ActionResult<IEnumerable<TagResponseTo>>> GetAll(
            [FromQuery] int pageNumber = 1,
            [FromQuery] int pageSize = 10)
        {
            var result = await _tagService.GetAllTagsAsync(pageNumber, pageSize);
            return Ok(result.Items);
        }

        [HttpGet("{id:long}")]
        public async Task<ActionResult<TagResponseTo>> GetById(long id)
        {
            var tag = await _tagService.GetTagByIdAsync(id);
            return Ok(tag);
        }

        [HttpPost]
        public async Task<ActionResult<TagResponseTo>> Create([FromBody] TagRequestTo dto)
        {
            var created = await _tagService.CreateTagAsync(dto);
            return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
        }

        [HttpPut]
        public async Task<ActionResult<TagResponseTo>> Update([FromBody] TagRequestTo dto)
        {
            if (dto.Id == null)
                return BadRequest("Id must be provided in the request body");
            var updated = await _tagService.UpdateTagAsync(dto.Id.Value, dto);
            return Ok(updated);
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> Delete(long id)
        {
            try
            {
                await _tagService.DeleteTagAsync(id);
                return NoContent();
            }
            catch (ValidationException ex)
            {
                return StatusCode(ex.HttpCode, new { error = ex.Message, code = ex.ErrorCode });
            }
        }
    }

}
