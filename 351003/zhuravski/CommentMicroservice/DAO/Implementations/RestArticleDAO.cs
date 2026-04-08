using Additions.DAO;
using CommentMicroservice.DAO.Interfaces;
using CommentMicroservice.DAO.Models;

namespace CommentMicroservice.DAO.Implementations;

class RestArticleDAO : IArticleDAO
{
    private readonly HttpClient httpClient;

    public RestArticleDAO(HttpClient httpClient)
    {
        this.httpClient = httpClient;
    }

    public async Task<ArticleModel[]> GetAllAsync()
    {
        ArticleModel[]? response = await httpClient.GetFromJsonAsync<ArticleModel[]>("api/v1.0/articles");
        return response ?? [];
    }

    public async Task<ArticleModel> AddNewAsync(ArticleModel model)
    {
        HttpResponseMessage? response = await httpClient.PostAsJsonAsync("api/v1.0/articles", model);
        if (!response.IsSuccessStatusCode)
        {
            throw new DAOUpdateException();
        }
        ArticleModel? result = await response.Content.ReadFromJsonAsync<ArticleModel>();
        if (null == result)
        {
            throw new DAOUpdateException("Object creation failure.");
        }
        return result;
    }

    public async Task DeleteAsync(long id)
    {
        HttpResponseMessage? response = await httpClient.DeleteAsync($"api/v1.0/articles/{id}");
        if (response.StatusCode == System.Net.HttpStatusCode.NotFound)
        {
            throw new DAOObjectNotFoundException();
        }
        response.EnsureSuccessStatusCode();
    }

    public async Task<ArticleModel> GetByIdAsync(long id)
    {
        try {
            ArticleModel? response = await httpClient.GetFromJsonAsync<ArticleModel>($"api/v1.0/articles/{id}");
            if (response == null) {
                throw new DAOObjectNotFoundException();
            }
            return response;
        }
        catch (HttpRequestException)
        {
            throw new DAOObjectNotFoundException();
        }
    }

    public async Task<ArticleModel> UpdateAsync(ArticleModel model)
    {
        HttpResponseMessage? response = await httpClient.PutAsJsonAsync($"api/v1.0/articles/{model.Id}", model);
        if (!response.IsSuccessStatusCode)
        {
            throw new DAOUpdateException();
        }
        ArticleModel? result = await response.Content.ReadFromJsonAsync<ArticleModel>();
        if (null == result) {
            throw new DAOUpdateException("Server returned empty response");
        }
        return result;
    }

    public async Task<Tuple<ArticleModel, long[]>> GetByIdWithMarksAsync(long id)
    {
        Tuple<ArticleModel, long[]>? response = await httpClient.GetFromJsonAsync<Tuple<ArticleModel, long[]>>($"api/v1.0/articles/{id}");
        if (response == null) {
            throw new DAOObjectNotFoundException();
        }
        return response;
    }
}