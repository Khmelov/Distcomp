using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;

namespace Task310RestApi.Interfaces
{
    public interface ILabelService
    {
        Task<IEnumerable<LabelResponseTo>> GetAllLabelsAsync();
        Task<LabelResponseTo?> GetLabelByIdAsync(long id);
        Task<LabelResponseTo> CreateLabelAsync(LabelRequestTo labelRequest);
        Task<LabelResponseTo?> UpdateLabelAsync(long id, LabelRequestTo labelRequest);
        Task<bool> DeleteLabelAsync(long id);
        Task<bool> ExistsAsync(long id);
        Task<IEnumerable<LabelResponseTo>> GetLabelsByNewsIdAsync(long newsId);
    }
}