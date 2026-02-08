namespace Domain.Models;

public class Note
{
    public long id { get; set; }
    public Story issueid { get; set; }
    public string content { get; set; }
}