public class StoryService : IStoryService
{
    private readonly IRepository<Story> _repository;
    private readonly IRepository<Writer> _writerRepository;

    public StoryService(IRepository<Story> repository, IRepository<Writer> writerRepository)
    {
        _repository = repository;
        _writerRepository = writerRepository;
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
        var writer = _writerRepository.GetById(request.WriterId);
        if (writer == null)
            throw new KeyNotFoundException($"Writer с id={request.WriterId} не найден");

        var duplicate = _repository.GetAll()
            .FirstOrDefault(s => s.Title == request.Title);
        if (duplicate != null)
            throw new ForbiddenException($"Story с title={request.Title} уже существует");

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