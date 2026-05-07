using Task310RestApi.Repositories;
using Task310RestApi.Interfaces;
using Task310RestApi.Models;

namespace Task310RestApi.Repositories
{
    public class InMemoryPostRepository : IRepository<Post>
    {
        private readonly Dictionary<long, Post> _posts = new();
        private long _idCounter = 1;

        public async Task<IEnumerable<Post>> GetAllAsync()
        {
            return await Task.FromResult(_posts.Values.AsEnumerable());
        }

        public async Task<Post?> GetByIdAsync(long id)
        {
            _posts.TryGetValue(id, out var post);
            return await Task.FromResult(post);
        }

        public async Task<Post> CreateAsync(Post entity)
        {
            if (entity.Id == 0)
            {
                entity.Id = _idCounter++;
            }
            _posts[entity.Id] = entity;
            return await Task.FromResult(entity);
        }

        public async Task<Post?> UpdateAsync(Post entity)
        {
            if (!_posts.ContainsKey(entity.Id))
            {
                return null;
            }
            _posts[entity.Id] = entity;
            return await Task.FromResult(entity);
        }

        public async Task<bool> DeleteAsync(long id)
        {
            return await Task.FromResult(_posts.Remove(id));
        }

        public async Task<bool> ExistsAsync(long id)
        {
            return await Task.FromResult(_posts.ContainsKey(id));
        }

        public async Task<IEnumerable<Post>> GetByNewsIdAsync(long newsId)
        {
            var result = _posts.Values.Where(p => p.NewsId == newsId);
            return await Task.FromResult(result);
        }
    }
}
