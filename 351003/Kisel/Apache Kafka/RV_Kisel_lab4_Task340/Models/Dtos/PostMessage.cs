namespace RV_Kisel_lab2_Task320.Models.Dtos;

public class PostMessage
{
    public string Action { get; set; } = string.Empty;
    public int Id { get; set; }
    public int NewsId { get; set; }
    public string Content { get; set; } = string.Empty;
    public string State { get; set; } = string.Empty;
}