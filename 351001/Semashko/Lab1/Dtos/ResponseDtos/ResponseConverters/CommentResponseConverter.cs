using lab_1.Domain;

namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public class CommentResponseConverter : BaseResponse<CommentResponseDto, Comment>
    {
        public CommentResponseDto ToDto(Comment entity)
        {
            CommentResponseDto dto = new CommentResponseDto();
            dto.content = entity.Content;
            dto.id = entity.Id;
            dto.storyId = entity.StoryId;
            return dto;
        }
    }
}
