public class WriterService : IWriterService
{
    private readonly IRepository<Writer> _repository;
    private readonly ICacheService _cache;

    public WriterService(IRepository<Writer> repository, ICacheService cache)
    {
        _repository = repository;
        _cache = cache;
    }

    public WriterResponseTo GetById(long id)
    {
        var cacheKey = $"writer:{id}";
        var cached = _cache.GetObjectAsync<WriterResponseTo>(cacheKey).GetAwaiter().GetResult();
        
        if (cached != null)
            return cached;

        var writer = _repository.GetById(id);
        if (writer == null)
            throw new KeyNotFoundException($"Writer с id={id} не найден");

        var response = ToResponse(writer);
        _cache.SetObjectAsync(cacheKey, response, TimeSpan.FromMinutes(5)).GetAwaiter().GetResult();
        
        return response;
    }

    public IEnumerable<WriterResponseTo> GetAll()
    {
        return _repository.GetAll().Select(ToResponse);
    }

    public WriterResponseTo Create(WriterRequestTo request)
    {
        var existing = _repository.GetAll()
            .FirstOrDefault(w => w.Login == request.Login);

        if (existing != null)
            throw new ForbiddenException($"Writer с login={request.Login} уже существует");

        var writer = ToModel(request);
        var created = _repository.Create(writer);
        return ToResponse(created);
    }

    public WriterResponseTo Update(WriterRequestTo request)
    {
        var writer = ToModel(request);
        var updated = _repository.Update(writer);
        var response = ToResponse(updated);

        _cache.RemoveAsync($"writer:{request.Id}").GetAwaiter().GetResult();
        
        return response;
    }

    public void Delete(long id)
    {
        if (!_repository.Delete(id))
            throw new KeyNotFoundException($"Writer с id={id} не найден");
        
        _cache.RemoveAsync($"writer:{id}").GetAwaiter().GetResult();
    }

    private Writer ToModel(WriterRequestTo dto) => new Writer
    {
        Id = dto.Id,
        Login = dto.Login,
        Password = dto.Password,
        Firstname = dto.Firstname,
        Lastname = dto.Lastname
    };

    private WriterResponseTo ToResponse(Writer model) => new WriterResponseTo
    {
        Id = model.Id,
        Login = model.Login,
        Firstname = model.Firstname,
        Lastname = model.Lastname
    };
}