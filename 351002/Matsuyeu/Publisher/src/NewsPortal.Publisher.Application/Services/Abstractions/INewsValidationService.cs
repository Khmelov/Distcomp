namespace Publisher.src.NewsPortal.Publisher.Application.Services.Abstractions
{
    public interface INewsValidationService
    {
        Task<bool> NewsExistsAsync(long newsId);
    }
}
