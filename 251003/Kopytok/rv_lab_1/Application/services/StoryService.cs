using Application.abstractions;
using Core;
using Database;
using DTO.requests;
using DTO.responces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Application.services
{
    public class StoryService(IRepository<Story> repository) : IStoryService
    {
        private IRepository<Story> _repository = repository;
        public async Task<StoryResponseTo> CreateAsync(StoryRequestTo request)
        {
            var entity = new Story() { Id = 0, Content = request.Content, Title = request.Title, 
                EditorId = request.EditorId, Created = DateTime.UtcNow, Modified = DateTime.UtcNow };
            var resp = await _repository.CreateAsync(entity);
            return new StoryResponseTo()
            {
                Id = resp.Id,
                Content = resp.Content,
                Title = resp.Title,
                EditorId = resp.EditorId,
                Created = resp.Created,
                Modified = resp.Modified
            };
        }

        public Task<bool> DeleteAsync(long id)
        {
            return _repository.DeleteAsync(id);
        }

        public async Task<IEnumerable<StoryResponseTo>> GetAllAsync()
        {
            var entities = await _repository.GetAllAsync();
            return (List<StoryResponseTo>)entities.Select(e => new StoryResponseTo()
            {
                Id = e.Id,
                Content = e.Content,
                Title = e.Title,
                EditorId = e.EditorId,
                Created = e.Created,
                Modified = e.Modified
            }).ToList();
        }

        public async Task<StoryResponseTo> GetByIdAsync(long id)
        {
            var entity = await _repository.GetByIdAsync(id);
            return new StoryResponseTo() {Id = entity.Id, Content = entity.Content,
                Title = entity.Title, EditorId = entity.EditorId, Created = entity.Created, Modified = entity.Modified };
        }

        public async Task<StoryResponseTo> UpdateAsync(long id, StoryRequestTo request)
        {
            var entity = await _repository.GetByIdAsync(id);
            var newEntity = new Story()
            {
                Id = id,
                Content = request.Content,
                Title = request.Title,
                EditorId = request.EditorId,
                Created = entity.Created,
                Modified = DateTime.UtcNow
            };
            var resp = await _repository.UpdateAsync(newEntity);
            return new StoryResponseTo()
            {
                Id = resp.Id,
                Content = resp.Content,
                EditorId = resp.EditorId,
                Created = resp.Created,
                Modified = resp.Modified,
                Title = resp.Title,
            };
        }
    }
}
