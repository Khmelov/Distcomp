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
        var writer = ToModel(request);
        
        // Hash the password using BCrypt
        writer.Password = BCrypt.Net.BCrypt.HashPassword(request.Password);
        
        // Set default role if not provided
        if (string.IsNullOrEmpty(writer.Role))
        {
            writer.Role = "CUSTOMER";
        }
        
        var created = _repository.Create(writer);
        return ToResponse(created);   
    }

    public WriterResponseTo Update(WriterRequestTo request)
    {
        var existing = _repository.GetById(request.Id);
        if (existing == null)
            throw new KeyNotFoundException($"Writer с id={request.Id} не найден");

        var writer = ToModel(request);
        
        // If password is provided, hash it
        if (!string.IsNullOrEmpty(request.Password))
        {
            writer.Password = BCrypt.Net.BCrypt.HashPassword(request.Password);
        }
        else
        {
            writer.Password = existing.Password;
        }
        
        // Preserve role if not provided
        if (string.IsNullOrEmpty(writer.Role))
        {
            writer.Role = existing.Role;
        }
        
        var updated = _repository.Update(writer);
        return ToResponse(updated);
    }

    public void Delete(long id)
    {
        if (!_repository.Delete(id))
            throw new KeyNotFoundException($"Writer с id={id} не найден");
    }

    public Writer? GetByLogin(string login)
    {
        // IMPORTANT: Return the LATEST user with this login (highest ID)
        return _repository.GetAll()
            .Where(w => w.Login == login)
            .OrderByDescending(w => w.Id)
            .FirstOrDefault();
    }

    public bool VerifyPassword(string password, string hashedPassword)
    {
        try
        {
            return BCrypt.Net.BCrypt.Verify(password, hashedPassword);
        }
        catch
        {
            return false;
        }
    }

    private Writer ToModel(WriterRequestTo dto) => new Writer
    {
        Id = dto.Id,
        Login = dto.Login,
        Password = dto.Password,
        Firstname = dto.Firstname,
        Lastname = dto.Lastname,
        Role = dto.Role ?? "CUSTOMER"
    };

    private WriterResponseTo ToResponse(Writer model) => new WriterResponseTo
    {
        Id = model.Id,
        Login = model.Login,
        Firstname = model.Firstname,
        Lastname = model.Lastname,
        Role = model.Role
    };
}