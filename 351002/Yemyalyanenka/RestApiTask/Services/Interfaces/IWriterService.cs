using RestApiTask.Models.DTOs;

namespace RestApiTask.Services.Interfaces
{
    public interface IWriterService
    {
        Task<IEnumerable<WriterResponseTo>> GetAllAsync();
        Task<WriterResponseTo> GetByIdAsync(long id);
        Task<WriterResponseTo> CreateAsync(WriterRequestTo request);
        Task<WriterResponseTo> UpdateAsync(long id, WriterRequestTo request);
        Task DeleteAsync(long id);
    }

}
