using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Interfaces;

public interface IArticleDAO : ILongIdDAO<ArticleModel>
{
    public Task<Tuple<ArticleModel, long[]>> GetByIdWithMarksAsync(long id);
}