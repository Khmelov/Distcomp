using System.Net.Http.Json;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Servicies;
using Infrastructure.Exceptions;
using Microsoft.Extensions.Configuration;

namespace Infrastructure.ServiceImplementation
{
    public class PostServiceProxy : IBaseService<PostRequestTo, PostResponseTo>
    {
        private readonly HttpClient _httpClient;
        private readonly string _baseUrl;

        public PostServiceProxy(HttpClient httpClient, IConfiguration configuration)
        {
            _httpClient = httpClient;
            _baseUrl = configuration["Microservices:PostServiceUrl"]
                       ?? throw new ArgumentNullException("PostServiceUrl is not configured");
        }

        public async Task<List<PostResponseTo>> GetAllAsync()
        {
            return await _httpClient.GetFromJsonAsync<List<PostResponseTo>>(_baseUrl) ?? new();
        }

        public async Task<PostResponseTo?> GetByIdAsync(int id)
        {
            var response = await _httpClient.GetAsync($"{_baseUrl}/{id}");
            if (response.StatusCode == System.Net.HttpStatusCode.NotFound) return null;

            return await response.Content.ReadFromJsonAsync<PostResponseTo>();
        }

        public async Task<PostResponseTo> CreateAsync(PostRequestTo entity)
        {
            var response = await _httpClient.PostAsJsonAsync(_baseUrl, entity);

            if (!response.IsSuccessStatusCode)
            {
                throw new BaseException((int)response.StatusCode, "Error while creating post in external service");
            }

            return await response.Content.ReadFromJsonAsync<PostResponseTo>();
        }

        public async Task<PostResponseTo?> UpdateAsync(PostRequestTo entity)
        {
            var response = await _httpClient.PutAsJsonAsync(_baseUrl, entity);
            if (response.StatusCode == System.Net.HttpStatusCode.NotFound) return null;

            return await response.Content.ReadFromJsonAsync<PostResponseTo>();
        }

        public async Task<bool> DeleteByIdAsync(int id)
        {
            var response = await _httpClient.DeleteAsync($"{_baseUrl}/{id}");
            return response.IsSuccessStatusCode;
        }
    }
}