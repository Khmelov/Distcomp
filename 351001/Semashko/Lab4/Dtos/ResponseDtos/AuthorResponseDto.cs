namespace lab_1.Dtos.ResponseDtos
{
    public class AuthorResponseDto : BaseResponseDto
    {
        public string? Login { get; set; }
        public string? Password { get; set; }
        public string? Firstname { get; set; }
        public string? Lastname { get; set; }

 
    }
}
