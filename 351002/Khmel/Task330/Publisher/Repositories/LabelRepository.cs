public class LabelRepository : IRepository<Label>
{
    private readonly AppDbContext _context;

    public LabelRepository(AppDbContext context)
    {
        _context = context;
    }

    public Label? GetById(long id) => _context.Labels.Find(id);

    public IEnumerable<Label> GetAll() => _context.Labels.ToList();

    public Label Create(Label entity)
    {
        _context.Labels.Add(entity);
        _context.SaveChanges();
        return entity;
    }

    public Label Update(Label entity)
    {
        var existing = _context.Labels.Find(entity.Id);
        if (existing == null)
            throw new KeyNotFoundException($"Label с id={entity.Id} не найден");

        existing.Name = entity.Name;
        _context.SaveChanges();
        return existing;
    }

    public bool Delete(long id)
    {
        var entity = _context.Labels.Find(id);
        if (entity == null) return false;

        _context.Labels.Remove(entity);
        _context.SaveChanges();
        return true;
    }
}