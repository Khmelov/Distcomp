using Additions.DAO;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO.Implementations;

public class DbArticleDAO : DbDAO<ArticleModel, DbSet<ArticleModel>>, IArticleDAO
{
    public DbArticleDAO(ApplicationContext db) : base(db, (x) => x.Articles) {}

    public async Task<Tuple<ArticleModel, long[]>> GetByIdWithMarksAsync(long id)
    {
        ArticleModel? model = await db.Articles.Include(a => a.ArticleMarks).FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new DAOObjectNotFoundException();
        }
        long[] leftMarkIds = [.. model.ArticleMarks.Select(m => m.MarkId)];
        return Tuple.Create(model, leftMarkIds);
    }
}