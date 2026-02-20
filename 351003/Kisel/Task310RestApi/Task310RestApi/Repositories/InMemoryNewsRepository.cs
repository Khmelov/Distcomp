using Task310RestApi.Repositories;
using Task310RestApi.Interfaces;
using Task310RestApi.Models;

namespace Task310RestApi.Repositories
{
    public class InMemoryNewsRepository : IRepository<News>
    {
        private readonly Dictionary<long, News> _news = new();
        private long _idCounter = 1;

        public async Task<IEnumerable<News>> GetAllAsync()
        {
            return await Task.FromResult(_news.Values.AsEnumerable());
        }

        public async Task<News?> GetByIdAsync(long id)
        {
            _news.TryGetValue(id, out var news);
            return await Task.FromResult(news);
        }

        public async Task<News> CreateAsync(News entity)
        {
            if (entity.Id == 0)
            {
                entity.Id = _idCounter++;
            }
            _news[entity.Id] = entity;
            return await Task.FromResult(entity);
        }

        public async Task<News?> UpdateAsync(News entity)
        {
            if (!_news.ContainsKey(entity.Id))
            {
                return null;
            }
            _news[entity.Id] = entity;
            return await Task.FromResult(entity);
        }

        public async Task<bool> DeleteAsync(long id)
        {
            return await Task.FromResult(_news.Remove(id));
        }

        public async Task<bool> ExistsAsync(long id)
        {
            return await Task.FromResult(_news.ContainsKey(id));
        }

        public async Task<IEnumerable<News>> GetByCreatorIdAsync(long creatorId)
        {
            var result = _news.Values.Where(n => n.CreatorId == creatorId);
            return await Task.FromResult(result);
        }

        public async Task<IEnumerable<News>> GetByLabelIdsAsync(List<long> labelIds)
        {
            var result = _news.Values.Where(n => n.LabelIds.Any(labelId => labelIds.Contains(labelId)));
            return await Task.FromResult(result);
        }

        public async Task<IEnumerable<News>> SearchAsync(string? title, string? content)
        {
            var query = _news.Values.AsEnumerable();
            
            if (!string.IsNullOrEmpty(title))
            {
                query = query.Where(n => n.Title.Contains(title, StringComparison.OrdinalIgnoreCase));
            }
            
            if (!string.IsNullOrEmpty(content))
            {
                query = query.Where(n => n.Content.Contains(content, StringComparison.OrdinalIgnoreCase));
            }
            
            return await Task.FromResult(query);
        }
    }
}
