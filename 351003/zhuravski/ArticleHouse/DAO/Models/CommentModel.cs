using System.ComponentModel.DataAnnotations.Schema;
using Additions.DAO;

namespace ArticleHouse.DAO.Models;

[Table("tbl_comment")]
public class CommentModel : LongIdModel<CommentModel>
{
    public long ArticleId {get; set;}
    public ArticleModel Article {get; set;} = null!;
    public string Content {get; set;} = default!;
}