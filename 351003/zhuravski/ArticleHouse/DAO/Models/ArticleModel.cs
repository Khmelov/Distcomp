using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;
using Additions.DAO;

namespace ArticleHouse.DAO.Models;

[Table("tbl_article")]
[Index(nameof(Title), IsUnique = true)]
public class ArticleModel : LongIdModel<ArticleModel>
{
    public long CreatorId {get; set;}
    public CreatorModel Creator {get; set;} = null!;
    public string Title {get; set;} = default!;
    public string Content {get; set;} = default!;
    //Отметки времени пока решено пропустить.

    public List<ArticleMark> ArticleMarks { get; set; } = [];
}