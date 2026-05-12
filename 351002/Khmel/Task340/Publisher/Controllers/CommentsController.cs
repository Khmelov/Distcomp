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
        public async Task<ActionResult<CommentResponseDto>> GetById(long id)
        {
            // Ждём до 3 секунд для первого появления
            var comment = await WaitForComment(id, 3000);
            if (comment == null)
                return NotFound(new { errorMessage = "Comment not found", errorCode = 40401 });
            
            return Ok(comment);
        }

        [HttpPost]
        public async Task<ActionResult<CommentResponseDto>> Create([FromBody] CommentRequestDto request)
        {
            if (!ModelState.IsValid)
                return BadRequest(ModelState);

            var commentId = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();

            // Сразу добавляем в кэш PENDING запись
            var pendingResponse = new CommentResponseDto
            {
                Id = commentId,
                StoryId = request.StoryId,
                Content = request.Content,
                Country = request.Country,
                State = "PENDING"
            };
            KafkaConsumerService.AddComment(pendingResponse);

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

            return CreatedAtAction(nameof(GetById), new { id = commentId }, pendingResponse);
        }

        [HttpPut("{id}")]
        public async Task<ActionResult<CommentResponseDto>> Update(
            long id,
            [FromBody] CommentRequestDto request)
        {
            var existingComment = await WaitForComment(id, 3000);
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
            _logger.LogInformation("Comment {Id} UPDATE sent to Kafka", id);

            // Ждём обновления контента
            var updatedComment = await WaitForUpdatedComment(id, request.Content, 3000);
            
            return Ok(updatedComment ?? existingComment);
        }

        [HttpDelete("{id}")]
        public async Task<IActionResult> Delete(long id)
        {
            var existingComment = await WaitForComment(id, 3000);
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
            _logger.LogInformation("Comment {Id} DELETE sent to Kafka", id);

            return NoContent();
        }

        private async Task<CommentResponseDto?> WaitForComment(long id, int timeoutMs)
        {
            var startTime = DateTime.UtcNow;
            
            while ((DateTime.UtcNow - startTime).TotalMilliseconds < timeoutMs)
            {
                var comment = KafkaConsumerService.GetComment(id);
                if (comment != null)
                    return comment;
                
                await Task.Delay(50);
            }
            
            return KafkaConsumerService.GetComment(id);
        }

        private async Task<CommentResponseDto?> WaitForUpdatedComment(long id, string expectedContent, int timeoutMs)
        {
            var startTime = DateTime.UtcNow;
            
            while ((DateTime.UtcNow - startTime).TotalMilliseconds < timeoutMs)
            {
                var comment = KafkaConsumerService.GetComment(id);
                if (comment != null && comment.Content == expectedContent)
                    return comment;
                
                await Task.Delay(50);
            }
            
            return KafkaConsumerService.GetComment(id);
        }
    }
}