namespace BlogService.Application.Interfaces.Services;

public interface IService<ID, Request, Response>
{
    Task<Response> CreateAsync(Request request);
    Task<Response?> GetAsync(ID id);
    Task<IEnumerable<Response>> GetAllAsync();
    Task<Response?> UpdateAsync(Request request);
    Task DeleteAsync(ID id);
}