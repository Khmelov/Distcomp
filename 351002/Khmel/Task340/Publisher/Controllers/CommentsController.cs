using Microsoft.AspNetCore.Mvc;
using Publisher.DTOs;
using Publisher.Services;

namespace Publisher.Controllers
{
    [ApiController]
    [Route("api/v1.0/comments")]
    public class CommentsController : ControllerBase
    {
        private readonly IKafkaProducerService _kafkaProducer;
        private readonly ILogger<CommentsController> _logger;

        public CommentsController(
            IKafkaProducerService kafkaProducer,
            ILogger<CommentsController> logger)
        {
            _kafkaProducer = kafkaProducer;
            _logger = logger;
        }

        [HttpGet]
        public ActionResult<IEnumerable<CommentResponseDto>> GetAll()
        {
            var comments = KafkaConsumerService.GetAllComments();
            return Ok(comments);
        }

        [HttpGet("{id}")]
        public ActionResult<CommentResponseDto> GetById(long id)
        {
            var comment = KafkaConsumerService.GetComment(id);
            if (comment == null)
                return NotFound(new { errorMessage = "Comment not found", errorCode = 40401 });
            
            return Ok(comment);
        }

        [HttpPost]
        public async Task<ActionResult<CommentResponseDto>> Create([FromBody] CommentRequestDto request)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            // Генерация ID
            var commentId = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();

            var kafkaMessage = new KafkaCommentMessage
            {
                Action = "CREATE",
                Data = new CommentData
                {
                    Id = commentId,
                    StoryId = request.StoryId,
                    Content = request.Content,
                    Country = request.Country,
                    State = "PENDING"
                }
            };

            await _kafkaProducer.SendAsync("InTopic", kafkaMessage);

            _logger.LogInformation("Comment {Id} sent to Kafka InTopic", commentId);

            // Возвращаем временный ответ
            var response = new CommentResponseDto
            {
                Id = commentId,
                StoryId = request.StoryId,
                Content = request.Content,
                Country = request.Country,
                State = "PENDING"
            };

            return CreatedAtAction(nameof(GetById), new { id = commentId }, response);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<CommentResponseDto>> Update(
            long id,
            [FromBody] CommentRequestDto request)
        {
            var existingComment = KafkaConsumerService.GetComment(id);
            if (existingComment == null)
                return NotFound(new { errorMessage = "Comment not found", errorCode = 40401 });

            var kafkaMessage = new KafkaCommentMessage
            {
                Action = "UPDATE",
                Data = new CommentData
                {
                    Id = id,
                    StoryId = request.StoryId,
                    Content = request.Content,
                    Country = request.Country,
                    State = existingComment.State
                }
            };

            await _kafkaProducer.SendAsync("InTopic", kafkaMessage);

            return Ok(existingComment);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(long id)
        {
            var existingComment = KafkaConsumerService.GetComment(id);
            if (existingComment == null)
                return NotFound(new { errorMessage = "Comment not found", errorCode = 40401 });

            var kafkaMessage = new KafkaCommentMessage
            {
                Action = "DELETE",
                Data = new CommentData
                {
                    Id = id,
                    StoryId = existingComment.StoryId
                }
            };

            await _kafkaProducer.SendAsync("InTopic", kafkaMessage);

            return NoContent();
        }
    }
}