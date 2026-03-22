using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO.Models;

[Table("tbl_mark")]
[Index(nameof(Name), IsUnique = true)]
public class MarkModel : Model<MarkModel>
{
    public string Name {get; set;} = default!;
    public List<ArticleMark> ArticleMarks { get; set; } = [];
}