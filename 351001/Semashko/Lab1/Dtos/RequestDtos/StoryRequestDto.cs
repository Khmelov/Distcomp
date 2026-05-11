namespace lab_1.Dtos.RequestDtos
{
    public class StoryRequestDto:BaseRequestDto
    {
        public long? authorId { get; set; }
        public string? title { get; set; }
        public string? content { get; set; }
    }
}
