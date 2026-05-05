using Task310RestApi.Interfaces;
using Task310RestApi.Models;
using Task310RestApi.Repositories;

namespace Task310RestApi.Repositories
{
    public class InMemoryCreatorRepository : IRepository<Creator>
    {
        private readonly Dictionary<long, Creator> _creators = new();
        private long _idCounter = 1;

        public async Task<IEnumerable<Creator>> GetAllAsync()
        {
            return await Task.FromResult(_creators.Values.AsEnumerable());
        }

        public async Task<Creator?> GetByIdAsync(long id)
        {
            _creators.TryGetValue(id, out var creator);
            return await Task.FromResult(creator);
        }

        public async Task<Creator> CreateAsync(Creator entity)
        {
            if (entity.Id == 0)
            {
                entity.Id = _idCounter++;
            }
            _creators[entity.Id] = entity;
            return await Task.FromResult(entity);
        }

        public async Task<Creator?> UpdateAsync(Creator entity)
        {
            if (!_creators.ContainsKey(entity.Id))
            {
                return null;
            }
            _creators[entity.Id] = entity;
            return await Task.FromResult(entity);
        }

        public async Task<bool> DeleteAsync(long id)
        {
            return await Task.FromResult(_creators.Remove(id));
        }

        public async Task<bool> ExistsAsync(long id)
        {
            return await Task.FromResult(_creators.ContainsKey(id));
        }

        public async Task<Creator?> FindByLoginAsync(string login)
        {
            var creator = _creators.Values.FirstOrDefault(c => c.Login.Equals(login, StringComparison.OrdinalIgnoreCase));
            return await Task.FromResult(creator);
        }
    }
}