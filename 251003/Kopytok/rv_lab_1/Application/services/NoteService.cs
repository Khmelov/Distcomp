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
    public class NoteService(IRepository<Note> repository) : INoteService
    {
        private readonly IRepository<Note> _repository = repository;
        public async Task<NoteResponseTo> CreateAsync(NoteRequestTo request)
        {
            Note entity = new() {Id = 0, StoryId = request.StoryId, Content = request.Content};
            var result = await _repository.CreateAsync(entity);
            return new NoteResponseTo() { Id = result.Id, StoryId = result.StoryId, Content = result.Content };
        }

        public Task<bool> DeleteAsync(long id)
        {
            return _repository.DeleteAsync(id);
        }

        public async Task<IEnumerable<NoteResponseTo>> GetAllAsync()
        {
            var result = await _repository.GetAllAsync();
            return (List<NoteResponseTo>)result.Select(x => new NoteResponseTo() {Id = x.Id, StoryId = x.StoryId, Content = x.Content }).ToList();
        }

        public async Task<NoteResponseTo> GetByIdAsync(long id)
        {
            var res = await _repository.GetByIdAsync(id);
            return new NoteResponseTo() {Id = res.Id, StoryId = res.StoryId, Content = res.Content};
        }

        public async Task<NoteResponseTo> UpdateAsync(long id, NoteRequestTo request)
        {
            var entity = new Note() {Id = id, StoryId = request.StoryId, Content = request.Content};
            var resp = await _repository.UpdateAsync(entity);
            return new NoteResponseTo() {Id = resp.Id, StoryId = resp.StoryId, Content = resp.Content};
        }
    }
}
