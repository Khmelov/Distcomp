using AutoMapper;
using Microsoft.Extensions.Caching.Distributed;
using Publisher.Dto;
using Publisher.Exceptions;
using Publisher.Model;
using Publisher.Repository;

namespace Publisher.Service {
    public class EditorService : BaseService<Editor, EditorRequestTo, EditorResponseTo> {
        public EditorService(
            IRepository<Editor> repository,
            IMapper mapper,
            ILogger<EditorService> logger,
            IDistributedCache cache)
            : base(repository, mapper, logger, cache) {
            _cacheKeyPrefix = "editor:";
        }

        public override async Task<EditorResponseTo> AddAsync(EditorRequestTo request) {
            _logger.LogInformation($"Creating editor with login: {request.Login}");

            var existingEditor = await FindByLoginAsync(request.Login);
            if (existingEditor != null) {
                throw new ForbiddenException($"Editor with login '{request.Login}' already exists");
            }

            var editor = _mapper.Map<Editor>(request);
            var created = await _repository.AddAsync(editor);
            var response = _mapper.Map<EditorResponseTo>(created);

            _logger.LogInformation($"Created editor with ID: {response.Id}");
            return response;
        }

        public override async Task<EditorResponseTo?> UpdateAsync(EditorRequestTo request) {
            _logger.LogInformation($"Updating editor ID: {request.Id} with login: {request.Login}");

            var existing = await _repository.GetByIdAsync(request.Id);
            if (existing == null) {
                _logger.LogWarning($"Editor {request.Id} not found");
                return null;
            }

            if (existing.Login != request.Login) {
                var editorWithSameLogin = await FindByLoginAsync(request.Login);
                if (editorWithSameLogin != null && editorWithSameLogin.Id != request.Id) {
                    throw new ForbiddenException($"Editor with login '{request.Login}' already exists");
                }
            }

            existing.Login = request.Login;
            existing.Password = request.Password;
            existing.Firstname = request.Firstname;
            existing.Lastname = request.Lastname;

            var updated = await _repository.UpdateAsync(existing);
            if (updated != null) {
                await _cache.RemoveAsync($"{_cacheKeyPrefix}{updated.Id}");
            }
            var response = _mapper.Map<EditorResponseTo>(updated);

            _logger.LogInformation($"Updated editor ID: {response.Id}");
            return response;
        }

        private async Task<Editor?> FindByLoginAsync(string login) {
            var allEditors = await _repository.GetAllAsync();
            return allEditors.FirstOrDefault(e => e.Login == login);
        }
    }
}