using Domain.Abstractions;

namespace Domain.Entities;

public class Author : BaseEntity {
    public string Login { get; set; } = "";
    public string Password { get; set; } = "";
    public string Firstname { get; set; } = "";
    public string Lastname { get; set; } = "";
}