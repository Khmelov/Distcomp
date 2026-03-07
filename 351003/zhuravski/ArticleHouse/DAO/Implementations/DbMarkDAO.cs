using Additions.DAO;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO.Implementations;

public class DbMarkDAO : DbDAO<MarkModel, DbSet<MarkModel>>, IMarkDAO
{
    public DbMarkDAO(ApplicationContext db) : base(db, (x) => x.Marks) {}

    public async Task ReleaseByIdsAsync(long[] ids)
    {
        if (ids.Length > 0)
        {
            await db.Marks
                    .Where(m => ids.Contains(m.Id))
                    .Where(m => !db.ArticleMarks.Any(m2 => m2.MarkId == m.Id))
                    .ExecuteDeleteAsync();
        }
    }

    public async Task<long[]> ReserveIdsByNamesAsync(string[] names)
    {
        IEnumerable<string> marks = names.Distinct();
        List<MarkModel> markModels = await db.Marks.Where(m => marks.Contains(m.Name)).ToListAsync();
        HashSet<string> foundMarks = [.. markModels.Select(m => m.Name)];
        string[] missingMarks = [.. marks.Where(m => !foundMarks.Contains(m))];
        foreach (string missing in missingMarks)
        {
            MarkModel newModel = new()
            {
                Name = missing
            };
            markModels.Add(newModel);
            await db.Marks.AddAsync(newModel);
        }
        try {
            await db.SaveChangesAsync();
        }
        catch (DbUpdateException)
        {
            throw new DAOUpdateException("Problems occured during reserving marks.");
        }
        return [.. markModels.Select(m => m.Id)];
    }
}