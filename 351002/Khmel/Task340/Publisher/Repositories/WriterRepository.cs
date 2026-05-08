public class WriterRepository : IRepository<Writer>
{
    private readonly AppDbContext _context;

    public WriterRepository(AppDbContext context)
    {
        _context = context;
    }

    public Writer? GetById(long id)
    {
        return _context.Writers.Find(id);
    }

    public IEnumerable<Writer> GetAll()
    {
        return _context.Writers.ToList();
    }

    public Writer Create(Writer entity)
    {
        _context.Writers.Add(entity);
        _context.SaveChanges(); 
        return entity;
    }

    public Writer Update(Writer entity)
    {
        var existing = _context.Writers.Find(entity.Id);
        if (existing == null)
            throw new KeyNotFoundException($"Writer с id={entity.Id} не найден");

        existing.Login = entity.Login;
        existing.Password = entity.Password;
        existing.Firstname = entity.Firstname;
        existing.Lastname = entity.Lastname;

        _context.SaveChanges(); 
        return existing;
    }

    public bool Delete(long id)
    {
        var entity = _context.Writers.Find(id);
        if (entity == null) return false;

        _context.Writers.Remove(entity);
        _context.SaveChanges();
        return true;
    }
}