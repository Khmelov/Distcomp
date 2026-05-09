public class WriterService : IWriterService
{
    private readonly IRepository<Writer> _repository;

    public WriterService(IRepository<Writer> repository)
    {
        _repository = repository;
    }

    public WriterResponseTo GetById(long id)
    {
        var writer = _repository.GetById(id);
        

        if (writer == null)
            throw new KeyNotFoundException($"Writer с id={id} не найден");
        
        return ToResponse(writer); 
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
        return ToResponse(updated);
    }

    public void Delete(long id)
    {
        if (!_repository.Delete(id))
            throw new KeyNotFoundException($"Writer с id={id} не найден");
    }

    // DTO in Model
    private Writer ToModel(WriterRequestTo dto) => new Writer
    {
        Id = dto.Id,
        Login = dto.Login,
        Password = dto.Password,
        Firstname = dto.Firstname,
        Lastname = dto.Lastname
    };

    // Model in DTO 
    private WriterResponseTo ToResponse(Writer model) => new WriterResponseTo
    {
        Id = model.Id,
        Login = model.Login,
        Firstname = model.Firstname,
        Lastname = model.Lastname
    };
}