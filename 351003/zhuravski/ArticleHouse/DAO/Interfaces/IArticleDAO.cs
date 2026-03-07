using Additions.DAO;
using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Interfaces;

public interface IArticleDAO : IBasicDAO<ArticleModel>
{
    public Task<Tuple<ArticleModel, long[]>> GetByIdWithMarksAsync(long id);
}