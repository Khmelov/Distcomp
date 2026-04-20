using RestApiTask.Models.DTOs;
using RestApiTask.Repositories;

namespace RestApiTask.Services.Interfaces
{
    public interface IMessageService
    {
        Task<IEnumerable<MessageResponseTo>> GetAllAsync(QueryOptions? options = null);
        Task<MessageResponseTo> GetByIdAsync(long id);
        Task<MessageResponseTo> CreateAsync(MessageRequestTo request);
        Task<MessageResponseTo> UpdateAsync(long id, MessageRequestTo request);
        Task DeleteAsync(long id);
    }
}
