namespace lab_1.Dtos.ResponseDtos
{
    public class CommentResponseDto:BaseResponseDto
    {
        public long? StoryId { get; set; }
        public string? Content { get; set; }
    }
}
