using Domain.Entities;
using Domain.Exceptions;
using Domain.Repository;

namespace Infrastructure.Repositories.InMemoryRepositories;

public class 
    InMemoryNewsRepository : INewsRepository
{
    private readonly Dictionary<long, News> _news = new ();
    private long _nextId = 1;

    public async Task<News?> AddNews(News news)
    {
        news.Id = _nextId;
        _news.Add(_nextId,news);
        return _news[_nextId++];
    }

    public async Task<News?> GetNews(long newsId)
    {
        if (_news.TryGetValue(newsId, out News news))
        {
            return news;
        }
        throw new NotFoundException("Id",$"{newsId}");
    }

    public async Task<News?> RemoveNews(long newsId)
    {
        if (_news.Remove(newsId, out News news))
        {
            return news;
        }
        throw new NotFoundException("Id",$"{newsId}");
    }

    public async Task<News?> UpdateNews(News news)
    {
        if (_news.ContainsKey(news.Id))
        {
            news.Modified = DateTime.Now;
            _news[news.Id] = news;
            return _news[news.Id];
        }
        throw new NotFoundException("Id",$"{news.Id}");
    }

    public async Task<IEnumerable<News?>?> GetAllNews()
    {
        return _news.Values.ToList();
    }
}