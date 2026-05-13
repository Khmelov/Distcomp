using lab_1.Domain;

namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public class StoryResponseConverter : BaseResponse<StoryResponseDto, Story>
    {
        public StoryResponseDto ToDto(Story entity)
        {
            var dto = new StoryResponseDto();
            dto.content = entity.Content;
            dto.title = entity.Title;
            dto.created = entity.Created;
            dto.authorId = entity.AuthorId;
            dto.id = entity.Id;
            dto.modified = entity.Modified;
            return dto;
        }
    }
}
