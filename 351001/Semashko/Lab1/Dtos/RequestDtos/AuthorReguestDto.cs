using Newtonsoft.Json;

namespace lab_1.Dtos.RequestDtos
{
    public class AuthorRequestDto:BaseRequestDto
    {
 
        public string? login { get; set; }
      
        public string? password { get; set; }

        public string? firstname { get; set; }

        public string? lastname { get; set; }


    }


}
