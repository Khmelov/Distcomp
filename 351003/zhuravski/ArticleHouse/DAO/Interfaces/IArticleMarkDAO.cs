namespace ArticleHouse.DAO.Interfaces;

public interface IArticleMarkDAO
{
    Task LinkArticleWithMarks(long articleId, long[] markIds);
}