using Additions.DAO;

namespace CommentMicroservice.DAO.Models;

public class CommentModel : LongIdModel<CommentModel>
{
    public long ArticleId {get; set;}
    public string Content {get; set;} = default!;
}