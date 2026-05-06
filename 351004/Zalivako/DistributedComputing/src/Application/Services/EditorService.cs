using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions.Application;
using Application.Interfaces;
using AutoMapper;
using Core.Entities;
using Microsoft.Extensions.Caching.Distributed;
using System.Text.Json;

namespace Application.Services
{
    public class EditorService : IEditorService
    {
        private readonly IMapper _mapper;
        private readonly IEditorRepository _editorRepository;
        private readonly IDistributedCache _cache;

        private const string AllEditorsCacheKey = "editors_all";
        private static string EditorByIdKey(long id) => $"editor_{id}";

        public EditorService(IMapper mapper, IEditorRepository repository, IDistributedCache cache)
        {
            _mapper = mapper;
            _editorRepository = repository;
            _cache = cache;
        }

        public async Task<EditorResponseTo> CreateEditor(EditorRequestTo request)
        {
            var editor = _mapper.Map<Editor>(request);
            editor.Password = HashPassword(request.Password!); // хешируем пароль

            try
            {
                var created = await _editorRepository.AddAsync(editor);
                var response = _mapper.Map<EditorResponseTo>(created);
                await InvalidateCacheAsync(created.Id);
                return response;
            }
            catch (InvalidOperationException ex)
            {
                throw new EditorAlreadyExistsException(ex.Message, ex);
            }
        }

        public async Task<EditorResponseTo> UpdateEditor(EditorRequestTo request)
        {
            var editor = _mapper.Map<Editor>(request);
            var existing = await _editorRepository.GetByIdAsync(editor.Id);
            if (existing == null)
                throw new EditorNotFoundException($"Editor {editor.Id} not found");

            // Если пароль передан, обновляем с хешированием
            if (!string.IsNullOrEmpty(request.Password))
                editor.Password = HashPassword(request.Password);
            else
                editor.Password = existing.Password; // сохраняем старый

            var updated = await _editorRepository.UpdateAsync(editor)
                ?? throw new EditorNotFoundException($"Update failed for editor {editor.Id}");
            var response = _mapper.Map<EditorResponseTo>(updated);
            await InvalidateCacheAsync(updated.Id);
            return response;
        }

        public async Task<IEnumerable<EditorResponseTo>> GetAllEditors()
        {
            var cached = await _cache.GetStringAsync(AllEditorsCacheKey);
            if (!string.IsNullOrEmpty(cached))
                return JsonSerializer.Deserialize<List<EditorResponseTo>>(cached)!;

            var editors = await _editorRepository.GetAllAsync();
            var result = _mapper.Map<List<EditorResponseTo>>(editors);
            await _cache.SetStringAsync(AllEditorsCacheKey, JsonSerializer.Serialize(result),
                new DistributedCacheEntryOptions { AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(5) });
            return result;
        }

        public async Task<EditorResponseTo> GetEditor(EditorRequestTo request)
        {
            long id = request.Id ?? 0;
            var cacheKey = EditorByIdKey(id);
            var cached = await _cache.GetStringAsync(cacheKey);
            if (!string.IsNullOrEmpty(cached))
                return JsonSerializer.Deserialize<EditorResponseTo>(cached)!;

            var editor = await _editorRepository.GetByIdAsync(id)
                ?? throw new EditorNotFoundException($"Editor {id} not found");
            var response = _mapper.Map<EditorResponseTo>(editor);
            await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(response),
                new DistributedCacheEntryOptions { AbsoluteExpirationRelativeToNow = TimeSpan.FromMinutes(5) });
            return response;
        }

        public async Task DeleteEditor(EditorRequestTo request)
        {
            var editor = _mapper.Map<Editor>(request);
            await _editorRepository.DeleteAsync(editor);
            await InvalidateCacheAsync(editor.Id);
        }

        private async Task InvalidateCacheAsync(long editorId)
        {
            await _cache.RemoveAsync(AllEditorsCacheKey);
            await _cache.RemoveAsync(EditorByIdKey(editorId));
        }

        private static string HashPassword(string password)
        {
            return BCrypt.Net.BCrypt.HashPassword(password);
        }
    }
}