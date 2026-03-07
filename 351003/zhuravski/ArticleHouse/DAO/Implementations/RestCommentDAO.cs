using Additions.DAO;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;

namespace ArticleHouse.DAO.Implementations;

class RestCommentDAO : ICommentDAO
{
    private readonly HttpClient httpClient;

    public RestCommentDAO(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    public async Task<CommentModel[]> GetAllAsync()
    {
        CommentModel[]? response = await httpClient.GetFromJsonAsync<CommentModel[]>("api/v1.0/comments");
        return response ?? [];
    }

    public async Task<CommentModel> AddNewAsync(CommentModel model)
    {
        HttpResponseMessage? response = await httpClient.PostAsJsonAsync("api/v1.0/comments", model);
        response.EnsureSuccessStatusCode();
        CommentModel? result = await response.Content.ReadFromJsonAsync<CommentModel>();
        if (null == result)
        {
            throw new DAOUpdateException("Object creation failure.");
        }
        return result;
    }

    public async Task DeleteAsync(long id)
    {
        HttpResponseMessage? response = await httpClient.DeleteAsync($"api/v1.0/comments/{id}");
        if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            throw new DAOObjectNotFoundException();
        }
        response.EnsureSuccessStatusCode();
    }

    public async Task<CommentModel> GetByIdAsync(long id)
    {
        CommentModel? response = await httpClient.GetFromJsonAsync<CommentModel>($"api/v1.0/comments/{id}");
        if (response == null) {
            throw new DAOObjectNotFoundException();
        }
        return response;
    }

    public async Task<CommentModel> UpdateAsync(CommentModel model)
    {
        HttpResponseMessage? response = await httpClient.PutAsJsonAsync($"api/v1.0/comments/{model.Id}", model);
        response.EnsureSuccessStatusCode();
        CommentModel? result = await response.Content.ReadFromJsonAsync<CommentModel>();
        if (null == result) {
            throw new InvalidOperationException("Server returned empty response");
        }
        return result;
    }
}