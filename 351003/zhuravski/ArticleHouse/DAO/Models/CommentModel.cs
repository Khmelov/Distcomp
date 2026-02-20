namespace ArticleHouse.DAO.Models;

public class CommentModel : Model<CommentModel>
{
    public long ArticleId {get; set;}
    public string Content {get; set;} = default!;
}