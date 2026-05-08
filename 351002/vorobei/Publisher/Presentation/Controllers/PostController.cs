using Microsoft.AspNetCore.Mvc;
using DataAccess.Models;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using Microsoft.AspNetCore.Authorization;
using System.Security.Claims;

namespace Presentation.Controllers
{
    [Route("api/v1.0/posts")]
    [ApiController]
    public class PostsControllerV1 : BaseController<Post, PostRequestTo, PostResponseTo>
    {
        public PostsControllerV1(IBaseService<PostRequestTo, PostResponseTo> service) : base(service)
        {
        }
    }

    [Route("api/v2.0/posts")]
    [ApiController]
    [Authorize]
    public class PostsControllerV2 : BaseController<Post, PostRequestTo, PostResponseTo>
    {
        private readonly IBaseService<StoryRequestTo, StoryResponseTo> _storyService;
        private readonly IBaseService<CreatorRequestTo, CreatorResponseTo> _creatorService;

        public PostsControllerV2(
            IBaseService<PostRequestTo, PostResponseTo> service,
            IBaseService<StoryRequestTo, StoryResponseTo> storyService,
            IBaseService<CreatorRequestTo, CreatorResponseTo> creatorService) : base(service)
        {
            _storyService = storyService;
            _creatorService = creatorService;
        }

        private async Task<int> GetCurrentUserIdAsync()
        {
            var login = User.FindFirstValue(ClaimTypes.NameIdentifier);
            var creators = await _creatorService.GetAllAsync();
            var currentCreator = creators.FirstOrDefault(c => c.Login == login);
            return currentCreator?.Id ?? 0;
        }

        [HttpPut]
        public override async Task<ActionResult<PostResponseTo>> UpdateAsync([FromBody] PostRequestTo entity)
        {
            if (!User.IsInRole("ADMIN"))
            {
                var existingPost = await _service.GetByIdAsync(entity.Id);
                if (existingPost == null) return NotFound();

                var story = await _storyService.GetByIdAsync(existingPost.StoryId);
                var currentUserId = await GetCurrentUserIdAsync();

                if (story == null || story.CreatorId != currentUserId)
                {
                    return StatusCode(403, new { error = "Access denied. You can only update posts in your own stories." });
                }
            }
            return await base.UpdateAsync(entity);
        }

        [HttpDelete("{id}")]
        public override async Task<ActionResult> Delete(int id)
        {
            if (!User.IsInRole("ADMIN"))
            {
                var existingPost = await _service.GetByIdAsync(id);
                if (existingPost != null)
                {
                    var story = await _storyService.GetByIdAsync(existingPost.StoryId);
                    var currentUserId = await GetCurrentUserIdAsync();

                    if (story != null && story.CreatorId != currentUserId)
                    {
                        return StatusCode(403, new { error = "Access denied. You can only delete posts in your own stories." });
                    }
                }
            }
            return await base.Delete(id);
        }
    }
}