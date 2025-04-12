using AutoMapper;
using DistComp.Controllers;
using DistComp.Data;
using DistComp.Models;
using Microsoft.AspNetCore.Mvc;
using System.Text.Json;
using System.Text.Json.Serialization;
using static System.Runtime.InteropServices.JavaScript.JSType;

namespace DistComp.Services.Implementation {
    public class CassandraCommentService : ICommentService, IDisposable {

        private DCContext context;
        private IMapper mapper;

        private HttpClient client;
        private string connectionString;
        private string apiPrefix;

        private JsonSerializerOptions options;

        public CassandraCommentService(DCContext context, IMapper mapper, IConfiguration configuration) {
            this.context = context;
            this.mapper = mapper;
            connectionString = configuration.GetConnectionString("discussion") 
                ?? throw new ApplicationException("'discussion' string is not found");

            client = new HttpClient();
            client.BaseAddress = new Uri(connectionString);

            // Get API perfix
            apiPrefix = "/" + "api/v1.0/comments";

            // Options
            options = new JsonSerializerOptions {
                PropertyNameCaseInsensitive = true
            };
        }

        public async Task<IEnumerable<CommentOutDto>> GetAll() {
            var response = await client.GetAsync(apiPrefix);
            string content = await response.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<IEnumerable<CommentOutDto>>(content, options)!;
        }

        public async Task<CommentOutDto?> Get(long id) {
            var response = await client.GetAsync(apiPrefix + "/" + id);
            string content = await response.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<CommentOutDto>(content, options)!;
        }

        public async Task<CommentOutDto> Create(CommentInDto data) {
            var topic = await context.Topics.FindAsync(data.TopicId);
            if (topic is null) {
                throw new KeyNotFoundException("Topic with specified ID does not exist");
            }

            var response = await client.PostAsync(apiPrefix, JsonContent.Create(data));
            response.EnsureSuccessStatusCode();
            string content = await response.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<CommentOutDto>(content, options)!;
        }

        public async Task<CommentOutDto> Update(Comment data) {
            var topic = await context.Topics.FindAsync(data.TopicId);
            if (topic is null) {
                throw new KeyNotFoundException("Topic with specified ID does not exist");
            }

            var response = await client.PutAsync(apiPrefix, JsonContent.Create(new {
                country = "by",
                id = data.Id,
                topicId = data.TopicId,
                content = data.Content,
            }));
            response.EnsureSuccessStatusCode();
            string content = await response.Content.ReadAsStringAsync();
            return JsonSerializer.Deserialize<CommentOutDto>(content, options)!;
        }

        public async Task Delete(long id) {
            var response = await client.DeleteAsync(apiPrefix + "/" + id);
            if (response.StatusCode == System.Net.HttpStatusCode.NotFound) {
                throw new KeyNotFoundException("hihi");
            }
            response.EnsureSuccessStatusCode();
        }

        public void Dispose() {
            client.Dispose();
        }
    }
}
