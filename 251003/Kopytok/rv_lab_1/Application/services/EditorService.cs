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

namespace Application.Services
{
    public class EditorService(IRepository<Editor> repository) : IEditorService
    {
        private readonly IRepository<Editor> _repository = repository;

        public async Task<EditorResponseTo> CreateAsync(EditorRequestTo request)
        {
            Editor entity = new() { Id = 0, Login = request.login, FirstName = request.firstname, LastName = request.lastname };
            var result = await _repository.CreateAsync(entity);
            return new EditorResponseTo() {id = result.Id, login = result.Login, firstname = result.FirstName, lastname = result.LastName};
        }

        public async Task<EditorResponseTo> GetByIdAsync(long id)
        {
            var result = await _repository.GetByIdAsync(id);
            return new EditorResponseTo() { id = result.Id, login = result.Login, firstname = result.FirstName, lastname = result.LastName };
        }

        public async Task<IEnumerable<EditorResponseTo>> GetAllAsync()
        {
            var result = await _repository.GetAllAsync();
            var list = (List<EditorResponseTo>)result.Select(x => new EditorResponseTo()
                { id = x.Id, login = x.Login, firstname = x.FirstName, lastname = x.LastName }).ToList();
            return list;
        }

        public async Task<EditorResponseTo> UpdateAsync(long id, EditorRequestTo request)
        {
            var entity = await _repository.GetByIdAsync(id);
            if (entity == null) 
                return null;

            entity.Login = request.login;
            entity.FirstName = request.firstname;
            entity.LastName = request.lastname;

            var result = await _repository.UpdateAsync(entity);
            return new EditorResponseTo() { id = result.Id, login = result.Login, firstname = result.FirstName, lastname = result.LastName };
        }

        public async Task<bool> DeleteAsync(long id) => await _repository.DeleteAsync(id);
    }
}
