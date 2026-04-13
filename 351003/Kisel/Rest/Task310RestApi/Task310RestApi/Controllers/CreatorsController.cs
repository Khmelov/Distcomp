using Microsoft.AspNetCore.Mvc;
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Interfaces;
using Task310RestApi.Exceptions; // Добавлено для краткости catch

namespace Task310RestApi.Controllers
{
    [Route("api/v1.0/creators")]
    [ApiController]
    public class CreatorsController : ControllerBase
    {
        private readonly ICreatorService _creatorService;

        public CreatorsController(ICreatorService creatorService)
        {
            _creatorService = creatorService;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<CreatorResponseTo>>> GetCreators()
        {
            var creators = await _creatorService.GetAllCreatorsAsync();
            return Ok(creators);
        }

        [HttpGet("{id}")]
        public async Task<ActionResult<CreatorResponseTo>> GetCreator(long id)
        {
            try
            {
                var creator = await _creatorService.GetCreatorByIdAsync(id);
                return Ok(creator);
            }
            catch (ResourceNotFoundException)
            {
                return NotFound();
            }
        }

        [HttpPost]
        public async Task<ActionResult<CreatorResponseTo>> CreateCreator([FromBody] CreatorRequestTo creatorRequest)
        {
            try
            {
                var createdCreator = await _creatorService.CreateCreatorAsync(creatorRequest);
                return CreatedAtAction(nameof(GetCreator), new { id = createdCreator.Id }, createdCreator);
            }
            catch (ValidationException ex)
            {
                return BadRequest(new ErrorResponse(ex.Message, ex.ErrorCode));
            }
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<CreatorResponseTo>> UpdateCreator(long id, [FromBody] CreatorRequestTo creatorRequest)
        {
            try
            {
                var updatedCreator = await _creatorService.UpdateCreatorAsync(id, creatorRequest);
                return Ok(updatedCreator);
            }
            catch (ResourceNotFoundException)
            {
                return NotFound();
            }
            catch (ValidationException ex)
            {
                return BadRequest(new ErrorResponse(ex.Message, ex.ErrorCode));
            }
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> DeleteCreator(long id)
        {
            try
            {
                await _creatorService.DeleteCreatorAsync(id);
                return NoContent();
            }
            catch (ResourceNotFoundException)
            {
                return NotFound();
            }
            catch (ValidationException ex)
            {
                return BadRequest(new ErrorResponse(ex.Message, ex.ErrorCode));
            }
        }

        [HttpGet("by-news/{newsId}")]
        public async Task<ActionResult<CreatorResponseTo>> GetCreatorByNewsId(long newsId)
        {
            try
            {
                var creator = await _creatorService.GetCreatorByNewsIdAsync(newsId);
                return Ok(creator);
            }
            catch (ResourceNotFoundException)
            {
                return NotFound();
            }
        }

        // Исправленный метод
        [HttpPut]
        public IActionResult UpdateCreatorNoId()
        {
            // Убрали длинный путь, так как using уже есть вверху
            return BadRequest(new ErrorResponse(
                "ID must be provided in the URL", 
                "40000"
            ));
        }
    }
}