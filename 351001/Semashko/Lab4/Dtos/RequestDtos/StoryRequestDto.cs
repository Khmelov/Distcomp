namespace lab_1.Dtos.RequestDtos
{
    public class StoryRequestDto:BaseRequestDto
    {
        public long? AuthorId { get; set; }
        public string? Title { get; set; }
        public string? Content { get; set; }

        public DateOnly Created { get; set; }
        
        public DateOnly Modified { get; set; }
    }
}
