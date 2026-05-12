using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;

namespace lab1.Services.Interfaces;

public interface ILabelService
{
    Task<LabelResponseTo> CreateAsync(LabelRequestTo request, CancellationToken cancellationToken = default);

    Task<LabelResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default);

    Task<PageResponseTo<LabelResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        string? nameContains,
        CancellationToken cancellationToken = default);

    Task<IReadOnlyList<LabelResponseTo>> GetAllAsync(CancellationToken cancellationToken = default);

    Task<LabelResponseTo> UpdateAsync(LabelRequestTo request, CancellationToken cancellationToken = default);

    Task DeleteAsync(long id, CancellationToken cancellationToken = default);
}
