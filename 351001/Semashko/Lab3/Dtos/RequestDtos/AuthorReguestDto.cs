using lab_1.Entities;
using Newtonsoft.Json;

namespace lab_1.Dtos.RequestDtos
{
    public class AuthorRequestDto:BaseRequestDto
    {
 
        public string? Login { get; set; }
      
        public string? Password { get; set; }

        public string? Firstname { get; set; }

        public string? Lastname { get; set; }
        


    }


}
