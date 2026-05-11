namespace ServerApp.Models;

public class ApiResponse
{
    public string Message { get; set; } = string.Empty;
    public string Version { get; set; } = "1.0";
    public DateTime ServerTime { get; set; } = DateTime.Now;
}