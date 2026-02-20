using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Interfaces;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers
{
    [ApiController]
    [Route("api/v1.0/[controller]")]
    public class PostsController : ControllerBase
    {
        private readonly IPostService _postService;
        private readonly ILogger<PostsController> _logger;

        public PostsController(IPostService postService, ILogger<PostsController> logger)
        {
            _postService = postService;
            _logger = logger;
        }

        [HttpPost]
        [ProducesResponseType(typeof(PostResponseTo), StatusCodes.Status201Created)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<PostResponseTo>> CreatePost([FromBody] PostRequestTo createPostRequest)
        {
            try
            {
                _logger.LogInformation("Creating post {request}", createPostRequest);

                PostResponseTo createdPost = await _postService.CreatePost(createPostRequest);

                return CreatedAtAction(
                    nameof(CreatePost),
                    new { id = createdPost.Id },
                    createdPost
                );
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating post");
                return StatusCode(500, "An error occurred while creating the post");
            }
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<PostResponseTo>), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<IEnumerable<PostResponseTo>>> GetAllPosts()
        {
            try
            {
                _logger.LogInformation("Getting all posts");

                IEnumerable<PostResponseTo> posts = await _postService.GetAllPosts();

                return Ok(posts);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting all posts");
                return StatusCode(500, "An error occurred while retrieving posts");
            }
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(PostResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<PostResponseTo>> GetPostById([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Getting post by id: {Id}", id);

                var getPostRequest = new PostRequestTo { Id = id };
                PostResponseTo post = await _postService.GetPost(getPostRequest);

                return Ok(post);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "Post not found with id: {Id}", id);
                return NotFound($"Post with id {id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting post by id: {Id}", id);
                return StatusCode(500, "An error occurred while retrieving the post");
            }
        }

        [ProducesResponseType(typeof(PostResponseTo), StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status400BadRequest)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<ActionResult<PostResponseTo>> UpdateNews(
            [FromBody] PostRequestTo updatePostRequest)
        {
            try
            {
                _logger.LogInformation("Updating post with id: {Id}", updatePostRequest.Id);

                var updatedPost = await _postService.UpdatePost(updatePostRequest);

                return Ok(updatedPost);
            }
            catch (NewNotFoundException ex)
            {
                _logger.LogWarning(ex, "Post not found for update with id: {Id}", updatePostRequest.Id);
                return NotFound($"Post with id {updatePostRequest.Id} not found");
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating post with id: {Id}", updatePostRequest.Id);
                return StatusCode(500, "An error occurred while updating the post");
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        [ProducesResponseType(StatusCodes.Status500InternalServerError)]
        public async Task<IActionResult> DeletePost([FromRoute] long id)
        {
            try
            {
                _logger.LogInformation("Deleting post with id: {Id}", id);

                var deletePostRequest = new PostRequestTo { Id = id };
                await _postService.DeletePost(deletePostRequest);

                return NoContent();
            }
            catch (PostNotFoundException ex)
            {
                _logger.LogWarning(ex, "Post not found for deletion with id: {Id}", id);
                return NotFound();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting post with id: {Id}", id);
                return StatusCode(500, "An error occurred while deleting the post");
            }
        }
    }
}
