public class StoryService : IStoryService
{
    private readonly IRepository<Story> _repository;

    public StoryService(IRepository<Story> repository)
    {
        _repository = repository;
    }

    public StoryResponseTo GetById(long id)
    {
        var story = _repository.GetById(id);
        if (story == null)
            throw new KeyNotFoundException($"Story с id={id} не найдена");
        return ToResponse(story);
    }

    public IEnumerable<StoryResponseTo> GetAll() =>
        _repository.GetAll().Select(ToResponse);

    public StoryResponseTo Create(StoryRequestTo request)
    {
        var story = ToModel(request);
        return ToResponse(_repository.Create(story));
    }

    public StoryResponseTo Update(StoryRequestTo request)
    {
        var story = ToModel(request);
        return ToResponse(_repository.Update(story));
    }

    public void Delete(long id)
    {
        if (!_repository.Delete(id))
            throw new KeyNotFoundException($"Story с id={id} не найдена");
    }

    private Story ToModel(StoryRequestTo dto) => new Story
    {
        Id = dto.Id,
        WriterId = dto.WriterId,
        Title = dto.Title,
        Content = dto.Content
    };

    private StoryResponseTo ToResponse(Story model) => new StoryResponseTo
    {
        Id = model.Id,
        WriterId = model.WriterId,
        Title = model.Title,
        Content = model.Content,
        Created = model.Created,
        Modified = model.Modified
    };
}