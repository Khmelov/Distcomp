using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using DataAccess.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;

namespace Presentation.Controllers
{
    [Route("api/v1.0/stories")]
    [ApiController]
    public class StoriesControllerV1 : BaseController<Story, StoryRequestTo, StoryResponseTo>
    {
        public StoriesControllerV1(IBaseService<StoryRequestTo, StoryResponseTo> service) : base(service)
        {
        }
    }

    [Route("api/v2.0/stories")]
    [ApiController]
    [Authorize]
    public class StoriesControllerV2 : BaseController<Story, StoryRequestTo, StoryResponseTo>
    {
        private readonly IBaseService<CreatorRequestTo, CreatorResponseTo> _creatorService;

        public StoriesControllerV2(
            IBaseService<StoryRequestTo, StoryResponseTo> service,
            IBaseService<CreatorRequestTo, CreatorResponseTo> creatorService) : base(service)
        {
            _creatorService = creatorService; // Внедряем сервис авторов для поиска Id по логину
        }

        private async Task<int> GetCurrentUserIdAsync()
        {
            var login = User.FindFirstValue(ClaimTypes.NameIdentifier);
            var creators = await _creatorService.GetAllAsync();
            var currentCreator = creators.FirstOrDefault(c => c.Login == login);
            return currentCreator?.Id ?? 0;
        }

        [HttpPut]
        public override async Task<ActionResult<StoryResponseTo>> UpdateAsync([FromBody] StoryRequestTo entity)
        {
            if (!User.IsInRole("ADMIN"))
            {
                var existingStory = await _service.GetByIdAsync(entity.Id);
                var currentUserId = await GetCurrentUserIdAsync();

                if (existingStory == null || existingStory.CreatorId != currentUserId)
                {
                    return StatusCode(403, new { error = "Access denied. You can only update your own stories." });
                }
            }
            return await base.UpdateAsync(entity);
        }

        [HttpDelete("{id}")]
        public override async Task<ActionResult> Delete(int id)
        {
            if (!User.IsInRole("ADMIN"))
            {
                var existingStory = await _service.GetByIdAsync(id);
                var currentUserId = await GetCurrentUserIdAsync();

                if (existingStory != null && existingStory.CreatorId != currentUserId)
                {
                    return StatusCode(403, new { error = "Access denied. You can only delete your own stories." });
                }
            }
            return await base.Delete(id);
        }
    }
}