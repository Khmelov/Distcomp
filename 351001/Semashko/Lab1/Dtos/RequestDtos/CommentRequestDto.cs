namespace lab_1.Dtos.RequestDtos
{
    public class CommentRequestDto : BaseRequestDto
    {
        public long? storyId { get; set; }
        public string? content { get; set; }
    }
}
