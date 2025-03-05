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
using static System.Net.Mime.MediaTypeNames;

namespace Application.services
{
    public class TagService(IRepository<Tag> repository) : ITagService
    {
        private readonly IRepository<Tag> _repository = repository;
        public async Task<TagResponseTo> CreateAsync(TagRequestTo request)
        {
            var entity = new Tag() { Id = 0, Name = request.Name };
            var resp = await _repository.CreateAsync(entity);
            return new TagResponseTo() { Id =  resp.Id, Name = resp.Name };
        }

        public Task<bool> DeleteAsync(long id)
        {
            return _repository.DeleteAsync(id);
        }

        public async Task<IEnumerable<TagResponseTo>> GetAllAsync()
        {
            var entities = await _repository.GetAllAsync();
            return (List<TagResponseTo>)entities.Select(s => new TagResponseTo() { Id = s.Id, Name = s.Name }).ToList();
        }

        public async Task<TagResponseTo> GetByIdAsync(long id)
        {
            var entity = await _repository.GetByIdAsync(id);
            return new TagResponseTo() { Id = entity.Id, Name = entity.Name };
        }

        public async Task<TagResponseTo> UpdateAsync(long id, TagRequestTo request)
        {
            var entity = new Tag() { Id = id, Name = request.Name };
            var resp = await _repository.UpdateAsync(entity);
            return new TagResponseTo() { Id =  resp.Id, Name = resp.Name };
        }
    }
}
