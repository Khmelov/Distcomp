using System.ComponentModel.DataAnnotations.Schema;
using Additions.DAO;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO.Models;

[Table("tbl_mark")]
[Index(nameof(Name), IsUnique = true)]
public class MarkModel : LongIdModel<MarkModel>
{
    public string Name {get; set;} = default!;
    public List<ArticleMark> ArticleMarks { get; set; } = [];
}