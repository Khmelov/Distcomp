using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Microsoft.AspNetCore.Mvc;

namespace API.Controllers
{
    [ApiController]
    [Route("api/v1.0/[controller]")]
    public class PostsController : ControllerBase
    {
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly ILogger<PostsController> _logger;

        public PostsController(
            IHttpClientFactory httpClientFactory,
            ILogger<PostsController> logger)
        {
            _httpClientFactory = httpClientFactory;
            _logger = logger;
        }

        private HttpClient CreateClient()
        {
            return _httpClientFactory.CreateClient("discussion");
        }

        [HttpPost]
        [ProducesResponseType(typeof(PostResponseTo), StatusCodes.Status201Created)]
        public async Task<ActionResult<PostResponseTo>> CreatePost(
            [FromBody] PostRequestTo createPostRequest)
        {
            try
            {
                _logger.LogInformation("Forwarding create post request");

                var client = CreateClient();

                var response = await client.PostAsJsonAsync("/api/v1.0/posts", createPostRequest);

                if (!response.IsSuccessStatusCode)
                {
                    return StatusCode((int)response.StatusCode);
                }

                var createdPost = await response.Content.ReadFromJsonAsync<PostResponseTo>();

                return CreatedAtAction(
                    nameof(GetPostById),
                    new { id = createdPost!.Id },
                    createdPost);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating post");
                return StatusCode(500);
            }
        }

        [HttpGet]
        [ProducesResponseType(typeof(IEnumerable<PostResponseTo>), StatusCodes.Status200OK)]
        public async Task<ActionResult<IEnumerable<PostResponseTo>>> GetAllPosts()
        {
            try
            {
                _logger.LogInformation("Forwarding get all posts");

                var client = CreateClient();

                var posts = await client.GetFromJsonAsync<IEnumerable<PostResponseTo>>("/api/v1.0/posts");

                return Ok(posts);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting posts");
                return StatusCode(500);
            }
        }

        [HttpGet("{id:long}")]
        [ProducesResponseType(typeof(PostResponseTo), StatusCodes.Status200OK)]
        public async Task<ActionResult<PostResponseTo>> GetPostById(long id)
        {
            try
            {
                _logger.LogInformation("Forwarding get post {Id}", id);

                var client = CreateClient();

                var response = await client.GetAsync($"/api/v1.0/posts/{id}");

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    return NotFound();
                }

                var post = await response.Content.ReadFromJsonAsync<PostResponseTo>();

                return Ok(post);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error getting post");
                return StatusCode(500);
            }
        }

        [HttpPut("{id:long}")]
        [ProducesResponseType(typeof(PostResponseTo), StatusCodes.Status200OK)]
        public async Task<ActionResult<PostResponseTo>> UpdatePost(
            long id,
            [FromBody] PostRequestTo updatePostRequest)
        {
            try
            {
                _logger.LogInformation("Forwarding update post {Id}", id);

                var client = CreateClient();

                var response = await client.PutAsJsonAsync(
                    $"/api/v1.0/posts/{id}",
                    updatePostRequest);

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    return NotFound();
                }

                var post = await response.Content.ReadFromJsonAsync<PostResponseTo>();

                return Ok(post);
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error updating post");
                return StatusCode(500);
            }
        }

        [HttpDelete("{id:long}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        public async Task<IActionResult> DeletePost(long id)
        {
            try
            {
                _logger.LogInformation("Forwarding delete post {Id}", id);

                var client = CreateClient();

                var response = await client.DeleteAsync($"/api/v1.0/posts/{id}");

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    return NotFound();
                }

                return NoContent();
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error deleting post");
                return StatusCode(500);
            }
        }
    }
}