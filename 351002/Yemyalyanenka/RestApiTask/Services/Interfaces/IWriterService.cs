using RestApiTask.Models.DTOs;
using RestApiTask.Repositories;

namespace RestApiTask.Services.Interfaces
{
    public interface IWriterService
    {
        Task<IEnumerable<WriterResponseTo>> GetAllAsync(QueryOptions? options = null);
        Task<WriterResponseTo> GetByIdAsync(long id);
        Task<WriterResponseTo> CreateAsync(WriterRequestTo request);
        Task<WriterResponseTo> UpdateAsync(long id, WriterRequestTo request);
        Task DeleteAsync(long id);
    }

}
