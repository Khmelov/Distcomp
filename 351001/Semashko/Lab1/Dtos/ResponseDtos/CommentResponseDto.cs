namespace lab_1.Dtos.ResponseDtos
{
    public class CommentResponseDto:BaseResponseDto
    {
        public long? storyId { get; set; }
        public string? content { get; set; }
    }
}
