

public interface IStoryService
{
    Task<StoryResponseTo> CreateStoryAsync(StoryRequestTo storyDto);
    Task<StoryResponseTo> GetStoryByIdAsync(int id);
    Task<IEnumerable<StoryResponseTo>> GetAllStorysAsync();
    Task<StoryResponseTo> UpdateStoryAsync(Story storyDto);
    Task<bool> DeleteStoryAsync(int id);
}
public class StoryService : IStoryService
{
    private readonly IGenericRepository<Story> _storyRepository;
    private readonly IGenericRepository<Editor> _editorRepository;

    public StoryService(
        IGenericRepository<Story> storyRepository,
        IGenericRepository<Editor> editorRepository,
        ILogger<StoryService> logger)
    {
        _storyRepository = storyRepository;
        _editorRepository = editorRepository;
    }

    public async Task<StoryResponseTo> CreateStoryAsync(StoryRequestTo storyDto)
    {
        var editor = await _editorRepository.GetByIdAsync(storyDto.EditorId);
        if (editor == null)
        {
            throw new KeyNotFoundException($"Editor with ID {storyDto.EditorId} does not exist");
        }

        var storyExists = await ((IGenericRepository<Story>)_storyRepository).ExistsByTitleAsync(storyDto.Title);
        if (storyExists)
        {
            throw new InvalidOperationException("Story with this story already exists");
        }

        var now = DateTime.UtcNow;
        var story = new Story
        {
            EditorId = storyDto.EditorId,
            Title = storyDto.Title,
            Content = storyDto.Content,
            Created = now,
            Modified = now
        };

        var createdStory = await _storyRepository.AddAsync(story);
        return MapToResponse(createdStory);
    }

    public async Task<bool> DeleteStoryAsync(int id)
    {
        var story = await _storyRepository.GetByIdAsync(id);
        if (story == null)
        {
            return false;
        }

        var deleted = await _storyRepository.DeleteAsync(id);

        return deleted;
    }

    public async Task<IEnumerable<StoryResponseTo>> GetAllStorysAsync()
    {
        var storys = await _storyRepository.GetAllAsync();
        return storys.Select(MapToResponse);
    }

    public async Task<StoryResponseTo> GetStoryByIdAsync(int id)
    {
        var story = await _storyRepository.GetByIdAsync(id);
        if (story == null)
        {
            throw new KeyNotFoundException("Story not found.");
        }

        return MapToResponse(story);
    }

    public async Task<StoryResponseTo> UpdateStoryAsync(Story storyDto)
    {
        var story = await _storyRepository.GetByIdAsync(storyDto.id);
        if (story == null)
            throw new KeyNotFoundException("Story not found.");

        story.Title = storyDto.Title;
        story.Created = storyDto.Created;
        story.EditorId = storyDto.EditorId;
        story.Content = storyDto.Content;
        story.Modified = storyDto.Modified;

        var updatedStory = await _storyRepository.UpdateAsync(story);
        return MapToResponse(updatedStory);
    }

    private StoryResponseTo MapToResponse(Story story)
    {
        return new StoryResponseTo
        {
            Id = story.id,
            Title = story.Title,
            Content = story.Content,
            EditorId = story.EditorId,
            created = story.Created,
            modified = story.Modified,
        };
    }
}



