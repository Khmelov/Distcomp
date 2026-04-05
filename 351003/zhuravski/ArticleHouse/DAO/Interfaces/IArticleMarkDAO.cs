namespace ArticleHouse.DAO.Interfaces;

public interface IArticleMarkDAO
{
    Task LinkArticleWithMarksAsync(long articleId, long[] markIds);
}