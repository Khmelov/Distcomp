using System.ComponentModel.DataAnnotations;

namespace TaskREST.Models;

public class Tag
{
    public long Id { get; set; }
    [MaxLength(32)] public string Name { get; set; } = string.Empty;
}