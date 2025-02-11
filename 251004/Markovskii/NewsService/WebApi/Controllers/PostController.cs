    using Application.DTO.Request.Post;
    using Application.DTO.Response.Post;
    using Application.Interfaces;
    using Microsoft.AspNetCore.Mvc;

    namespace WebApi.Controllers;

    [ApiController]
    [Route("/api/v1.0/posts")]
    public class PostController (IPostService _postService) : ControllerBase
    {
        [HttpGet]
        public async Task<IActionResult> GetAllPost()
        {
            var post = await _postService.GetPosts(new PostRequestToGetAll());
            return Ok(post);
        }
        
        [HttpGet("{id:long}")]
        public async Task<IActionResult> GetPostById(long id)
        {
            PostResponseToGetById post = await _postService.GetPostById(new PostRequestToGetById() {Id = id});
            return Ok(post);
        }

        [HttpPost]
        public async Task<IActionResult> CreatePost(PostRequestToCreate postRequestToCreate)
        {
            var post = await _postService.CreatePost(postRequestToCreate);
            return CreatedAtAction(nameof(GetPostById), new { id = post.Id }, post);
        }
        
        [HttpPut]
        public async Task<IActionResult> UpdatePost(PostRequestToFullUpdate postModel)
        {
            var post = await _postService.UpdatePost(postModel);
            return Ok(post);
        }
        
        [HttpDelete("{id:long}")]
        public async Task<IActionResult> DeletePost(long id)
        {
            var post = await _postService.DeletePost(new PostRequestToDeleteById(){Id = id});
            return NoContent();
        }
    }