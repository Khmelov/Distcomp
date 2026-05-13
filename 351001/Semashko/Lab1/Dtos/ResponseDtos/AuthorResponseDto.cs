namespace lab_1.Dtos.ResponseDtos
{
    public class AuthorResponseDto : BaseResponseDto
    {
        public string? login { get; set; }
        public string? password { get; set; }
        public string? firstname { get; set; }
        public string? lastname { get; set; }

 
    }
}
