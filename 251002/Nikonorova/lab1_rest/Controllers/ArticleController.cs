using distcomp.DTOs;
using distcomp.Services;
using Microsoft.AspNetCore.Mvc;

namespace distcomp.Controllers
{
    [ApiController]
    [Route("api/v1.0/articles")]
    public class ArticleController : ControllerBase
    {
        private readonly ArticleService _articleService;

        public ArticleController(ArticleService articleService)
        {
            _articleService = articleService;
        }

        
        [HttpPost]
        public IActionResult CreateArticle([FromBody] ArticleRequestTo articleRequest)
        {
            var articleResponse = _articleService.CreateArticle(articleRequest);
            return CreatedAtAction(nameof(GetArticle), new { id = articleResponse.Id }, articleResponse);
        }

        
        [HttpGet("{id}")]
        public IActionResult GetArticle(long id)
        {
            var articleResponse = _articleService.GetArticleById(id);
            return articleResponse != null ? Ok(articleResponse) : NotFound();
        }

        
        [HttpGet]
        public IActionResult GetAllarticles()
        {
            var articleResponses = _articleService.GetAllArticles();
            return Ok(articleResponses);
        }

        
        [HttpPut]
        public IActionResult UpdateArticle(long id, [FromBody] ArticleRequestTo articleRequest)
        {
            

            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            if (articleRequest == null || articleRequest.Id <= 0)
            {
                return BadRequest("Invalid topic data.");
            }

            var updatedUser = _articleService.UpdateArticle(articleRequest.Id, articleRequest);
            if (updatedUser == null)
            {
                return NotFound();
            }

            return Ok(updatedUser);
        }

       
        [HttpDelete("{id}")]
        public IActionResult DeleteArticle(long id)
        {
            var isDeleted = _articleService.DeleteArticle(id);
            return isDeleted ? NoContent() : NotFound();
        }
    }
}
