using DataAccess.Models;
using BusinessLogic.Repositories;

namespace BusinessLogic.Servicies
{
    public interface IBaseService<TEntityRequest, TEntityResponse> where TEntityRequest : class
                                                                   where TEntityResponse : class
    {
        Task<List<TEntityResponse>> GetAllAsync();
        Task<TEntityResponse?> GetByIdAsync(int id);
        Task<TEntityResponse> CreateAsync(TEntityRequest entity);
        Task<TEntityResponse?> UpdateAsync(TEntityRequest entity);
        Task<bool> DeleteByIdAsync(int id);
    }
}
