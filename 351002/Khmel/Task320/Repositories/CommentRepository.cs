public class CommentRepository : IRepository<Comment>
{
    private readonly AppDbContext _context;

    public CommentRepository(AppDbContext context)
    {
        _context = context;
    }

    public Comment? GetById(long id) => _context.Comments.Find(id);

    public IEnumerable<Comment> GetAll() => _context.Comments.ToList();

    public Comment Create(Comment entity)
    {
        _context.Comments.Add(entity);
        _context.SaveChanges();
        return entity;
    }

    public Comment Update(Comment entity)
    {
        var existing = _context.Comments.Find(entity.Id);
        if (existing == null)
            throw new KeyNotFoundException($"Comment с id={entity.Id} не найден");

        existing.Content = entity.Content;
        existing.StoryId = entity.StoryId;
        _context.SaveChanges();
        return existing;
    }

    public bool Delete(long id)
    {
        var entity = _context.Comments.Find(id);
        if (entity == null) return false;

        _context.Comments.Remove(entity);
        _context.SaveChanges();
        return true;
    }
}