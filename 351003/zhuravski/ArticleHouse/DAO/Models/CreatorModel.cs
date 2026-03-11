using System.ComponentModel.DataAnnotations.Schema;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.DAO.Models;

[Table("tbl_creator")]
[Index(nameof(Login), IsUnique = true)]
public class CreatorModel : Model<CreatorModel>
{
    public CreatorModel() {}
    public string Password {get; set;} = default!;
    public string Login {get; set;} = default!;
    [Column("firstname")]
    public string FirstName {get; set;} = default!;
    [Column("lastname")]
    public string LastName {get; set;} = default!;
};