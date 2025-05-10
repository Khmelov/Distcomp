using Distributed_Computing_Lab_1.Models;
using Distributed_Computing_Lab_1.Repositories.Interfaces;

namespace Distributed_Computing_Lab_1.Repositories.Implementations;

public class InMemoryTopicRepository : BaseInMemoryRepository<Topic>, ITopicRepository
{
    /*
    // Индекс для поиска по title истории
    private readonly Dictionary<string, long> _titleIndex = [];

    public override async Task<Topic> CreateAsync(Topic entity)
    {
        if (_titleIndex.ContainsKey(entity.Title))
        {
            throw new ConflictException(ErrorCodes.StoryAlreadyExists,
                ErrorMessages.StoryAlreadyExists(entity.Title));
        }

        var story = await base.CreateAsync(entity);
        _titleIndex.Add(story.Title, story.Id);

        return story;
    }

    public override async Task<Topic?> UpdateAsync(Topic entity)
    {
        if (_titleIndex.TryGetValue(entity.Title, out long value) && value != entity.Id)
        {
            throw new ConflictException(ErrorCodes.StoryAlreadyExists,
                ErrorMessages.StoryAlreadyExists(entity.Title));
        }

        var updatedStory = await base.UpdateAsync(entity);
        if (updatedStory != null)
        {
            if (_titleIndex.ContainsKey(entity.Title) && _titleIndex[entity.Title] == entity.Id)
            {
                return updatedStory;
            }

            _titleIndex.Remove(entity.Title);
            _titleIndex.Add(updatedStory.Title, updatedStory.Id);
        }

        return updatedStory;
    }

    public override async Task<Topic?> DeleteAsync(long id)
    {
        var story = await base.DeleteAsync(id);

        if (story != null)
        {
            _titleIndex.Remove(story.Title);
        }

        return story;
    }
    */
}