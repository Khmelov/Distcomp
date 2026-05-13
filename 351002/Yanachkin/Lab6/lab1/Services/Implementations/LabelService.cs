using System.Linq.Expressions;
using AutoMapper;
using lab1.Common.Sorting;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Models.Entities;
using lab1.Repositories.Interfaces;
using lab1.Services.Interfaces;

namespace lab1.Services.Implementations;

public class LabelService : ILabelService
{
    private readonly IEntityRepository<Label> _repository;
    private readonly IMapper _mapper;

    public LabelService(IEntityRepository<Label> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<LabelResponseTo> CreateAsync(LabelRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);
        var label = _mapper.Map<Label>(request);
        label.Id = 0;
        var created = await _repository.AddAsync(label, cancellationToken);
        return _mapper.Map<LabelResponseTo>(created);
    }

    public async Task<LabelResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var label = await _repository.GetByIdAsync(id, cancellationToken)
            ?? throw new KeyNotFoundException("Label not found");

        return _mapper.Map<LabelResponseTo>(label);
    }

    public async Task<PageResponseTo<LabelResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        string? nameContains,
        CancellationToken cancellationToken = default)
    {
        Expression<Func<Label, bool>>? filter = null;
        if (!string.IsNullOrWhiteSpace(nameContains))
        {
            var needle = nameContains;
            filter = l => l.Name.Contains(needle);
        }

        var order = LabelSortResolvers.Resolve(sort);
        var result = await _repository.GetPagedAsync(filter, order, page, size, cancellationToken);

        return new PageResponseTo<LabelResponseTo>
        {
            Content = result.Content.Select(l => _mapper.Map<LabelResponseTo>(l)).ToList(),
            TotalElements = result.TotalElements,
            TotalPages = result.TotalPages,
            Number = result.Number,
            Size = result.Size
        };
    }

    public async Task<IReadOnlyList<LabelResponseTo>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        var entities = await _repository.GetAllAsync(cancellationToken);
        return entities.Select(l => _mapper.Map<LabelResponseTo>(l)).ToList();
    }

    public async Task<LabelResponseTo> UpdateAsync(LabelRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);

        var existing = await _repository.GetByIdAsync(request.Id, cancellationToken)
            ?? throw new KeyNotFoundException("Label not found");

        existing.Name = request.Name;
        var updated = await _repository.UpdateAsync(existing, cancellationToken);
        return _mapper.Map<LabelResponseTo>(updated);
    }

    public Task DeleteAsync(long id, CancellationToken cancellationToken = default)
        => _repository.DeleteByIdAsync(id, cancellationToken);

    private static void Validate(LabelRequestTo request)
    {
        if (string.IsNullOrWhiteSpace(request.Name))
            throw new ArgumentException("Name must not be empty");
    }
}
