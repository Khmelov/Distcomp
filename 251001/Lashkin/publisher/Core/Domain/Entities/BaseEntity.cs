using System.ComponentModel.DataAnnotations.Schema;

namespace Domain.Entities;

public abstract class BaseEntity
{
    [Column("id")]
    public long Id { get; set; }
}