using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.ResponseDtos;
using lab_1.Services;
using Microsoft.AspNetCore.Mvc;

namespace lab_1.Controllers
{
    [ApiController]
    [Route("api/v1.0/comments")]
    public class CommentsController : ControllerBase
    {
        private readonly BaseService<CommentRequestDto,CommentResponseDto> _service;
        public CommentsController(BaseService<CommentRequestDto,CommentResponseDto> commentService)
        {
            _service = commentService;
        }

        [HttpGet]
        [ProducesResponseType(StatusCodes.Status200OK)]
        public ActionResult<List<CommentResponseDto>> GetComments() => Ok(_service.GetAll());

        [HttpPost]
        [ProducesResponseType(StatusCodes.Status201Created)]
        public ActionResult<CommentResponseDto> CreateComment([FromBody]CommentRequestDto dto)
        {
            var created = _service.Create(dto);
            return CreatedAtAction(nameof(GetComment), new { id = created?.id }, created);
        }

        [HttpDelete("{id}")]
        [ProducesResponseType(StatusCodes.Status204NoContent)]
        public ActionResult DeleteComment(long id)
        {
            return _service.Delete(id) ? NoContent() : NotFound();
        }

        [HttpPut]
        public ActionResult<CommentResponseDto> UpdateComment([FromBody] CommentRequestDto dto)
        {
            var updated = _service.Update(dto);
            return updated == null ? NotFound() : Ok(updated);
        }

        [HttpGet("{id}")]
        [ProducesResponseType(StatusCodes.Status200OK)]
        [ProducesResponseType(StatusCodes.Status404NotFound)]
        public ActionResult<CommentResponseDto> GetComment(long id)
        {
            var result = _service.Read(id);
            return result == null ? NotFound() : Ok(result);
        }
    }
}
