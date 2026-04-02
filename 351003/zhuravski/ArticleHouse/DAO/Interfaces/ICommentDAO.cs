using ArticleHouse.DAO.Models;
using Additions.DAO;

namespace ArticleHouse.DAO.Interfaces;

public interface ICommentDAO : ILongIdDAO<CommentModel>
{
    Task DeleteByArticleIdAsync(long articleId);
}