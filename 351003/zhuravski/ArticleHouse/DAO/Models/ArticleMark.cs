namespace ArticleHouse.DAO.Models;

public class ArticleMark
{
    public long ArticleId { get; set; }
    public ArticleModel Article { get; set; } = default!;

    public long MarkId { get; set; }
    public MarkModel Mark { get; set; } = default!;
}