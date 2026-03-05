using System.ComponentModel.DataAnnotations.Schema;

namespace ArticleHouse.DAO.Models;

[Table("tbl_comment")]
public class CommentModel : Model<CommentModel>
{
    public long ArticleId {get; set;}
    public ArticleModel Article {get; set;} = null!;
    public string Content {get; set;} = default!;
}