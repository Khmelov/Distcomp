using lab_1.Domain;

namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public class ListStoryResponseConverter
    {
        private StoryResponseConverter storyResponseConverter = new StoryResponseConverter();
        public IEnumerable<StoryResponseDto> StorysResponse(IEnumerable<Story> list)
        {
            foreach (Story node in list)
                yield return storyResponseConverter.ToDto(node);
        }
    }
}
