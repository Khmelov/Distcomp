using CommentMicroservice.DAO.Models;
using Additions.DAO;

namespace CommentMicroservice.DAO.Interfaces;

public interface IArticleDAO : ILongIdDAO<ArticleModel>
{
    public Task<Tuple<ArticleModel, long[]>> GetByIdWithMarksAsync(long id);
}