using Task310RestApi.Repositories;
using Task310RestApi.Interfaces;
using Task310RestApi.Models;

namespace Task310RestApi.Repositories
{
    public class InMemoryLabelRepository : IRepository<Label>
    {
        private readonly Dictionary<long, Label> _labels = new();
        private long _idCounter = 1;

        public async Task<IEnumerable<Label>> GetAllAsync()
        {
            return await Task.FromResult(_labels.Values.AsEnumerable());
        }

        public async Task<Label?> GetByIdAsync(long id)
        {
            _labels.TryGetValue(id, out var label);
            return await Task.FromResult(label);
        }

        public async Task<Label> CreateAsync(Label entity)
        {
            if (entity.Id == 0)
            {
                entity.Id = _idCounter++;
            }
            _labels[entity.Id] = entity;
            return await Task.FromResult(entity);
        }

        public async Task<Label?> UpdateAsync(Label entity)
        {
            if (!_labels.ContainsKey(entity.Id))
            {
                return null;
            }
            _labels[entity.Id] = entity;
            return await Task.FromResult(entity);
        }

        public async Task<bool> DeleteAsync(long id)
        {
            return await Task.FromResult(_labels.Remove(id));
        }

        public async Task<bool> ExistsAsync(long id)
        {
            return await Task.FromResult(_labels.ContainsKey(id));
        }

        public async Task<IEnumerable<Label>> GetByNamesAsync(List<string> names)
        {
            var result = _labels.Values.Where(l => names.Contains(l.Name, StringComparer.OrdinalIgnoreCase));
            return await Task.FromResult(result);
        }
    }
}
