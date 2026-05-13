namespace lab_1.Dtos.ResponseDtos;

public class AuthTokenResponseDto
{
    public string AccessToken { get; set; } = string.Empty;

    public string TypeToken { get; set; } = "Bearer";
}
