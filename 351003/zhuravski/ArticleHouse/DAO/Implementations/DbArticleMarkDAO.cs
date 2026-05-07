using Additions.DAO;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO.Implementations;

class DbArticleMarkDAO : IArticleMarkDAO
{
    private readonly ApplicationContext db;

    public DbArticleMarkDAO(ApplicationContext db)
    {
        this.db = db;
    }

    public async Task LinkArticleWithMarksAsync(long articleId, long[] markIds)
    {
        foreach (long markId in markIds)
        {
            db.ArticleMarks.Add(new ArticleMark
            {
                ArticleId = articleId,
                MarkId = markId
            });
        }
        try
        {
            await db.SaveChangesAsync();
        }
        catch (DbUpdateException)
        {
            throw new DAOUpdateException("Couldn't link article with marks.");
        }
    }
}