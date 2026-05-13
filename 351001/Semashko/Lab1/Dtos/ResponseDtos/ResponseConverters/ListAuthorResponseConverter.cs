using lab_1.Domain;

namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public class ListAuthorResponseConverter
    {
        private AuthorResponseConverter authorResponseConverter = new AuthorResponseConverter();
        public IEnumerable<AuthorResponseDto?> AuthorsResponse(IEnumerable<Author> list) {
            foreach (Author node  in list) 
                yield return authorResponseConverter.ToDto(node);
            }
    }
}
