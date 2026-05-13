using lab_1.Domain;

namespace lab_1.Dtos.RequestDtos.RequestConverters
{
    public class AuthorRequestConverter:BaseRequest<Author,AuthorRequestDto>
    {
        public  Author FromDto (AuthorRequestDto dto,long? id)=>new Author(id,dto?.login,dto?.password,dto?.firstname,dto?.lastname);
    }
}
