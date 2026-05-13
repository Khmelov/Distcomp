namespace lab_1.Dtos.ResponseDtos
{
    public class StoryResponseDto:BaseResponseDto
    {
        public long? authorId { get; set; }
        public string? title { get; set; }
        public string? content { get; set; }
        public DateTime created { get; set; }
        public DateTime modified { get; set; }
    }
}
