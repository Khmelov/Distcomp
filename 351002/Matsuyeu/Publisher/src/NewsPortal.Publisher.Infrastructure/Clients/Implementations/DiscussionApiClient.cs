using System.Net;
using System.Text;
using System.Text.Json;
using Discussion.src.NewsPortal.Discussion.Application.Dtos.ResponseTo;
using Publisher.src.NewsPortal.Publisher.Application.Dtos.RequestTo;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;
using Publisher.src.NewsPortal.Publisher.Domain.Exceptions;
using Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Abstractions;

namespace Publisher.src.NewsPortal.Publisher.Infrastructure.Clients.Implementations
{
    public class DiscussionApiClient : IDiscussionApiClient
    {
        private readonly HttpClient _httpClient;
        private readonly ILogger<DiscussionApiClient> _logger;
        private readonly JsonSerializerOptions _jsonOptions;

        public DiscussionApiClient(HttpClient httpClient, ILogger<DiscussionApiClient> logger)
        {
            _httpClient = httpClient;
            _logger = logger;
            _jsonOptions = new JsonSerializerOptions
            {
                PropertyNameCaseInsensitive = true,
                PropertyNamingPolicy = JsonNamingPolicy.CamelCase
            };
        }

        public async Task<IEnumerable<NoteResponseTo>> GetAllNotesAsync()
        {
            try
            {
                var response = await _httpClient.GetAsync("api/v1.0/notes");
                response.EnsureSuccessStatusCode();

                var content = await response.Content.ReadAsStringAsync();
                return JsonSerializer.Deserialize<IEnumerable<NoteResponseTo>>(content, _jsonOptions)
                       ?? new List<NoteResponseTo>();
            }
            catch (HttpRequestException ex)
            {
                _logger.LogError(ex, "Error getting all notes from Discussion API");
                throw new Exception("Failed to get notes from Discussion service", ex);
            }
        }

        public async Task<NoteResponseTo?> GetNoteByIdAsync(long id)
        {
            try
            {
                var response = await _httpClient.GetAsync($"api/v1.0/notes/{id}");

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    return null;
                }

                response.EnsureSuccessStatusCode();

                var content = await response.Content.ReadAsStringAsync();
                return JsonSerializer.Deserialize<NoteResponseTo>(content, _jsonOptions);
            }
            catch (HttpRequestException ex)
            {
                _logger.LogError(ex, "Error getting note {Id} from Discussion API", id);
                throw new Exception($"Failed to get note {id} from Discussion service", ex);
            }
        }

        public async Task<NoteResponseTo> CreateNoteAsync(NoteRequestTo noteRequest)
        {
            try
            {
                var json = JsonSerializer.Serialize(noteRequest, _jsonOptions);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var response = await _httpClient.PostAsync("api/v1.0/notes", content);

                if (response.StatusCode == HttpStatusCode.NotFound)
                    throw new NotFoundException($"News with ID {noteRequest.NewsId} does not exist");

                response.EnsureSuccessStatusCode();

                var responseContent = await response.Content.ReadAsStringAsync();
                var createdNote = JsonSerializer.Deserialize<NoteResponseTo>(responseContent, _jsonOptions);

                if (createdNote == null)
                {
                    throw new Exception("Failed to deserialize created note");
                }

                return createdNote;
            }
            catch (HttpRequestException ex)
            {
                _logger.LogError(ex, "Error creating note in Discussion API");
                throw new Exception("Failed to create note in Discussion service", ex);
            }
        }

        public async Task<NoteResponseTo> UpdateNoteAsync(NoteRequestTo noteRequest)
        {
            try
            {
                var json = JsonSerializer.Serialize(noteRequest, _jsonOptions);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var response = await _httpClient.PutAsync("api/v1.0/notes", content);

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    throw new Exception($"Note with id {noteRequest.Id} not found");
                }

                response.EnsureSuccessStatusCode();

                var responseContent = await response.Content.ReadAsStringAsync();
                var updatedNote = JsonSerializer.Deserialize<NoteResponseTo>(responseContent, _jsonOptions);

                if (updatedNote == null)
                {
                    throw new Exception("Failed to deserialize updated note");
                }

                return updatedNote;
            }
            catch (HttpRequestException ex)
            {
                _logger.LogError(ex, "Error updating note {Id} in Discussion API", noteRequest.Id);
                throw new Exception($"Failed to update note {noteRequest.Id} in Discussion service", ex);
            }
        }

        public async Task<bool> DeleteNoteAsync(long id)
        {
            try
            {
                var response = await _httpClient.DeleteAsync($"api/v1.0/notes/{id}");

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    return false;
                }

                response.EnsureSuccessStatusCode();
                return true;
            }
            catch (HttpRequestException ex)
            {
                _logger.LogError(ex, "Error deleting note {Id} from Discussion API", id);
                throw new Exception($"Failed to delete note {id} from Discussion service", ex);
            }
        }

        /// <summary>
        /// Удаляет все заметки для указанной новости (каскадное удаление)
        /// </summary>
        /// <param name="newsId">ID новости</param>
        /// <returns>True если удаление успешно, иначе False</returns>
        public async Task<bool> DeleteNotesByNewsIdAsync(long newsId)
        {
            try
            {
                _logger.LogInformation("Deleting all notes for news {NewsId}", newsId);

                var response = await _httpClient.DeleteAsync($"api/v1.0/notes/by-news/{newsId}");

                if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
                {
                    _logger.LogWarning("News {NewsId} not found in Discussion API", newsId);
                    return false;
                }

                response.EnsureSuccessStatusCode();

                _logger.LogInformation("Successfully deleted all notes for news {NewsId}", newsId);
                return true;
            }
            catch (HttpRequestException ex)
            {
                _logger.LogError(ex, "Error deleting notes for news {NewsId} from Discussion API", newsId);
                return false;
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Unexpected error deleting notes for news {NewsId}", newsId);
                return false;
            }
        }
    }
}
