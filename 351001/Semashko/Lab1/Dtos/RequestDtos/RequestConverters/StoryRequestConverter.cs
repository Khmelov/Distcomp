using lab_1.Domain;

namespace lab_1.Dtos.RequestDtos.RequestConverters
{
    public class StoryRequestConverter : BaseRequest<Story, StoryRequestDto>
    {
        public Story FromDto(StoryRequestDto dto, long? id)
        {
            return new Story(id, dto.authorId, dto.title, dto.content,DateTime.Now, DateTime.Now);
        }
    }
}
