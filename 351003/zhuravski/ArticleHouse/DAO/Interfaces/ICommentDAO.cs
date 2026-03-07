using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Interfaces;

public interface ICommentDAO : ILongIdDAO<CommentModel>
{
    Task DeleteByArticleId(long articleId);
}