using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Mvc;

namespace lab_1.Controllers
{
    [ApiController]
    [Route("api/v1.0/stories")]
    public class StoriesController : ControllerBase
    {
        private readonly BaseService<StoryRequestDto,StoryResponseDto> storyService;
        public StoriesController(BaseService<StoryRequestDto,StoryResponseDto> storyService)
        {
            this.storyService = storyService;
        }

        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<List<StoryResponseDto>> GetStories() => Ok(storyService.GetAll());

        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        public ActionResult<StoryResponseDto> CreateStory([FromBody]StoryRequestDto dto)
        {
            var created = storyService.Create(dto);
            // return 201 with location header pointing to the new resource
            return CreatedAtAction(nameof(GetStory), new { id = created?.id }, created);
        }

        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        public ActionResult DeleteStory(long id)
        {
            return storyService.Delete(id) ? NoContent() : NotFound();
        }

        [HttpPut]
        public ActionResult<StoryResponseDto> UpdateStory([FromBody]StoryRequestDto dto)
        {
            var updated = storyService.Update(dto);
            return updated == null ? NotFound() : Ok(updated);
        }

        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public ActionResult<StoryResponseDto> GetStory(long id)
        {
            var result = storyService.Read(id);
            return result == null ? NotFound() : Ok(result);
        }
    }
}
