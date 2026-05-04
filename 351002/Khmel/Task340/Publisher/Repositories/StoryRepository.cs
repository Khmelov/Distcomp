using Microsoft.EntityFrameworkCore;

public class StoryRepository : IRepository<Story>
{
    private readonly AppDbContext _context;

    public StoryRepository(AppDbContext context)
    {
        _context = context;
    }

    public Story? GetById(long id)
    {
        return _context.Stories
            .Include(s => s.Labels) 
            .FirstOrDefault(s => s.Id == id);
    }

    public IEnumerable<Story> GetAll()
    {
        return _context.Stories
            .Include(s => s.Labels)
            .ToList();
    }

    public Story Create(Story entity)
    {
        entity.Created = DateTime.UtcNow;
        entity.Modified = DateTime.UtcNow;
        _context.Stories.Add(entity);
        _context.SaveChanges();
        return entity;
    }

    public Story Update(Story entity)
    {
        var existing = _context.Stories
            .Include(s => s.Labels)
            .FirstOrDefault(s => s.Id == entity.Id);

        if (existing == null)
            throw new KeyNotFoundException($"Story с id={entity.Id} не найдена");

        existing.Title = entity.Title;
        existing.Content = entity.Content;
        existing.WriterId = entity.WriterId;
        existing.Modified = DateTime.UtcNow;

        _context.SaveChanges();
        return existing;
    }

    public bool Delete(long id)
    {
        var entity = _context.Stories.Find(id);
        if (entity == null) return false;

        _context.Stories.Remove(entity);
        _context.SaveChanges();
        return true;
    }
}