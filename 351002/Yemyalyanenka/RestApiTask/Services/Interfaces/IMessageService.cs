using RestApiTask.Models.DTOs;

namespace RestApiTask.Services.Interfaces
{
    public interface IMessageService
    {
        Task<IEnumerable<MessageResponseTo>> GetAllAsync();
        Task<MessageResponseTo> GetByIdAsync(long id);
        Task<MessageResponseTo> CreateAsync(MessageRequestTo request);
        Task<MessageResponseTo> UpdateAsync(long id, MessageRequestTo request);
        Task DeleteAsync(long id);
    }
}
