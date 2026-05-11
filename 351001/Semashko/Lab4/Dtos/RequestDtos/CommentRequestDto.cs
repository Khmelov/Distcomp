namespace lab_1.Dtos.RequestDtos
{
    public class CommentRequestDto : BaseRequestDto
    {
        public long? StoryId { get; set; }
        public string? Content { get; set; }
    }
}
