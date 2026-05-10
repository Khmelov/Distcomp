using ArticleHouse.DAO.Models;
using Additions.DAO;

namespace ArticleHouse.DAO.Interfaces;

public interface IArticleDAO : ILongIdDAO<ArticleModel>
{
    public Task<Tuple<ArticleModel, long[]>> GetByIdWithMarksAsync(long id);
}