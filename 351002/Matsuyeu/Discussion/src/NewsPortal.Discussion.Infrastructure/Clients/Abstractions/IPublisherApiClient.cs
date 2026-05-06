namespace Discussion.src.NewsPortal.Discussion.Infrastructure.Clients.Abstractions
{
    public interface IPublisherApiClient
    {
        Task<bool> NewsExistsAsync(long newsId);
    }
}
