using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using Shared.Domain.Interfaces;

namespace BlogService.Domain.Entities;

[Table("tbl_user")]
public class User<Id> : IEntity<Id>
{
    [Key]
    [Column("id")]
    public Id ID { get; set; }

    [Required]
    [Column("login")]
    // В БД должен быть Unique Index на это поле
    public string Login { get; set; }

    [Required]
    [Column("password")]
    public string Password { get; set; } // Здесь будет храниться BCrypt хэш

    [Column("firstname")]
    public string FirstName { get; set; }

    [Column("lastname")]
    public string LastName { get; set; }
    
    [Column("role")]
    public string Role { get; set; } = "CUSTOMER";

    public ICollection<Story<Id>> Stories { get; set; } = new List<Story<Id>>();
}