using Domain.Entities;

namespace Domain.Repository;

public interface INewsRepository
{
    Task<News?> AddNews(News news);
    Task<News?> GetNews(long newsId);
    Task<News?> RemoveNews(long newsId);
    Task<News?> UpdateNews(News news);

    Task<IEnumerable<News?>?> GetAllNews();
}