namespace RV_Kisel_lab2_Task320.Models.Dtos
{
    public class PostDto
    {
        public int Id { get; set; } // Изменено на int
        public int NewsId { get; set; }
        public string Content { get; set; }
        public DateTimeOffset Created { get; set; }
    }

    public class CreatePostDto
    {
        public int NewsId { get; set; }
        public string Content { get; set; }
    }
    
    public class NewsResponseDto : NewsDto
    {
        public IEnumerable<PostDto> Posts { get; set; } = new List<PostDto>();
    }
}