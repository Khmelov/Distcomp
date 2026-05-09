using Distcomp.Application.DTOs;
using Distcomp.Application.Interfaces;
using System.Net.Http.Json;

namespace Distcomp.Application.Services
{
    public class NoteRemoteService : INoteService
    {
        private readonly HttpClient _httpClient;

        public NoteRemoteService(HttpClient httpClient)
        {
            _httpClient = httpClient;
        }

        public NoteResponseTo Create(NoteRequestTo request)
        {
            var msg = new
            {
                issueId = request.IssueId,
                content = request.Content,
                country = "BY"
            };

            var response = _httpClient.PostAsJsonAsync("notes", msg).Result;
            response.EnsureSuccessStatusCode();

            return response.Content.ReadFromJsonAsync<NoteResponseTo>().Result!;
        }

        public IEnumerable<NoteResponseTo> GetAll()
        {
            return _httpClient.GetFromJsonAsync<IEnumerable<NoteResponseTo>>("notes").Result ?? new List<NoteResponseTo>();
        }

        public NoteResponseTo? GetById(long id)
        {
            return GetAll().FirstOrDefault(n => n.Id == id);
        }

        public bool Delete(long id)
        {
            return true;
        }

        public NoteResponseTo? Update(long id, NoteRequestTo request)
        {
            var msg = new
            {
                id = id,
                issueId = request.IssueId,
                content = request.Content,
                country = "BY"
            };

            var response = _httpClient.PutAsJsonAsync($"notes/{id}", msg).Result;

            if (!response.IsSuccessStatusCode) return null;

            return response.Content.ReadFromJsonAsync<NoteResponseTo>().Result;
        }
    }
}