using Microsoft.AspNetCore.Mvc;
using WebApplication1.DTO;
using WebApplication1.Repository;
using WebApplication1.Service;

namespace WebApplication1.Controller
{
    [ApiController]
    [Route("api/v1.0/topics")]
    public class TopicController : ControllerBase
    {
        private readonly ITopicService _topicService;
        public TopicController(ITopicService topicService) => _topicService = topicService;

        [HttpGet]
        public async Task<ActionResult<IEnumerable<TopicResponseTo>>> GetAll(
            [FromQuery] int pageNumber = 1,
            [FromQuery] int pageSize = 10,
            [FromQuery] string? sortBy = null,
            [FromQuery] string? filter = null)
        {
            try
            {
                var result = await _topicService.GetAllTopicsAsync(pageNumber, pageSize, sortBy, filter);
                return Ok(result.Items);
            }
            catch (ValidationException ex)
            {
                return StatusCode(ex.HttpCode, new { error = ex.Message, code = ex.ErrorCode });
            }
            catch (Exception)
            {
                return StatusCode(500, new { error = "Internal server error", code = "50000" });
            }
        }


        [HttpGet("{id:long}")]
        public async Task<ActionResult<TopicResponseTo>> GetById(long id)
        {
            try
            {
                var topic = await _topicService.GetTopicByIdAsync(id);
                return Ok(topic);
            }
            catch (ValidationException ex)
            {
                return StatusCode(ex.HttpCode, new { error = ex.Message, code = ex.ErrorCode });
            }
        }

        [HttpPost]
        public async Task<ActionResult<TopicResponseTo>> Create([FromBody] TopicRequestTo dto)
        {
            try
            {
                var created = await _topicService.CreateTopicAsync(dto);
                return CreatedAtAction(nameof(GetById), new { id = created.Id }, created);
            }
            catch (ValidationException ex)
            {
                return StatusCode(ex.HttpCode, new { error = ex.Message, code = ex.ErrorCode });
            }
            catch (Exception)
            {
                return StatusCode(500, new { error = "Internal server error", code = "50000" });
            }
        }

        [HttpPut]
        public async Task<ActionResult<TopicResponseTo>> Update([FromBody] TopicRequestTo dto)
        {
            if (dto.Id == null || dto.Id <= 0)
            {
                return BadRequest(new { error = "Invalid ID", code = "40001" });
            }

            try
            {
                var updated = await _topicService.UpdateTopicAsync(dto.Id.Value, dto);
                return Ok(updated);
            }
            catch (ValidationException ex)
            {
                return StatusCode(ex.HttpCode, new { error = ex.Message, code = ex.ErrorCode });
            }
        }

        [HttpDelete("{id:long}")]
        public async Task<IActionResult> Delete(long id)
        {
            try
            {
                await _topicService.DeleteTopicAsync(id);
                return NoContent();
            }
            catch (ValidationException ex)
            {
                return StatusCode(ex.HttpCode, new { error = ex.Message, code = ex.ErrorCode });
            }
        }
    }
}
