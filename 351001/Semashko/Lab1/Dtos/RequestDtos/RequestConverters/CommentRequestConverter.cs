using lab_1.Domain;

namespace lab_1.Dtos.RequestDtos.RequestConverters
{
    public class CommentRequestConverter : BaseRequest<Comment, CommentRequestDto>
    {
        public Comment FromDto(CommentRequestDto dto, long? id)
        {
            return new Comment(id, dto.storyId, dto.content);
        }
    }
}
