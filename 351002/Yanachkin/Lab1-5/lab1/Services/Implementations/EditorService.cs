using System.Linq.Expressions;
using AutoMapper;
using lab1.Common.Sorting;
using lab1.Errors;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Models.Entities;
using lab1.Repositories.Interfaces;
using lab1.Services.Interfaces;

namespace lab1.Services.Implementations;

public class EditorService : IEditorService
{
    private readonly IEntityRepository<Editor> _repository;
    private readonly IMapper _mapper;

    public EditorService(IEntityRepository<Editor> repository, IMapper mapper)
    {
        _repository = repository;
        _mapper = mapper;
    }

    public async Task<EditorResponseTo> CreateAsync(EditorRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);
        await EnsureLoginAvailableAsync(request.Login, excludeEditorId: null, cancellationToken);

        var editor = _mapper.Map<Editor>(request);
        editor.Id = 0;
        var created = await _repository.AddAsync(editor, cancellationToken);

        return _mapper.Map<EditorResponseTo>(created);
    }

    public async Task<EditorResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var editor = await _repository.GetByIdAsync(id, cancellationToken)
            ?? throw new KeyNotFoundException("Editor not found");

        return _mapper.Map<EditorResponseTo>(editor);
    }

    public async Task<PageResponseTo<EditorResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        string? loginContains,
        CancellationToken cancellationToken = default)
    {
        Expression<Func<Editor, bool>>? filter = string.IsNullOrWhiteSpace(loginContains)
            ? null
            : e => e.Login.Contains(loginContains);

        var order = EditorSortResolvers.Resolve(sort);

        var result = await _repository.GetPagedAsync(filter, order, page, size, cancellationToken);

        return new PageResponseTo<EditorResponseTo>
        {
            Content = result.Content.Select(e => _mapper.Map<EditorResponseTo>(e)).ToList(),
            TotalElements = result.TotalElements,
            TotalPages = result.TotalPages,
            Number = result.Number,
            Size = result.Size
        };
    }

    public async Task<IReadOnlyList<EditorResponseTo>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        var entities = await _repository.GetAllAsync(cancellationToken);
        return entities.Select(e => _mapper.Map<EditorResponseTo>(e)).ToList();
    }

    public async Task<EditorResponseTo> UpdateAsync(EditorRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);
        await EnsureLoginAvailableAsync(request.Login, request.Id, cancellationToken);

        var existing = await _repository.GetByIdAsync(request.Id, cancellationToken)
            ?? throw new KeyNotFoundException("Editor not found");

        existing.Login = request.Login;
        existing.Password = request.Password;
        existing.FirstName = request.Firstname;
        existing.LastName = request.Lastname;

        var updated = await _repository.UpdateAsync(existing, cancellationToken);
        return _mapper.Map<EditorResponseTo>(updated);
    }

    public Task DeleteAsync(long id, CancellationToken cancellationToken = default)
        => _repository.DeleteByIdAsync(id, cancellationToken);

    private async Task EnsureLoginAvailableAsync(
        string login,
        long? excludeEditorId,
        CancellationToken cancellationToken)
    {
        Expression<Func<Editor, bool>> filter = excludeEditorId.HasValue
            ? e => e.Login == login && e.Id != excludeEditorId.Value
            : e => e.Login == login;

        var found = await _repository.GetPagedAsync(filter, null, 0, 1, cancellationToken);
        if (found.TotalElements > 0)
            throw new EditorLoginAlreadyExistsException();
    }

    private static void Validate(EditorRequestTo request)
    {
        if (string.IsNullOrWhiteSpace(request.Login))
            throw new ArgumentException("Login must not be empty");

        if (request.Login.Length < 3)
            throw new ArgumentException("Login is too short");

        if (request.Login.Length > 64)
            throw new ArgumentException("Login exceeds maximum length");

        if (string.IsNullOrWhiteSpace(request.Password))
            throw new ArgumentException("Password must not be empty");

        if (request.Password.Length < 8)
            throw new ArgumentException("Password is too short");

        if (string.IsNullOrWhiteSpace(request.Firstname))
            throw new ArgumentException("FirstName must not be empty");

        if (request.Firstname.Length < 2)
            throw new ArgumentException("FirstName is too short");

        if (string.IsNullOrWhiteSpace(request.Lastname))
            throw new ArgumentException("LastName must not be empty");

        if (request.Lastname.Length < 2)
            throw new ArgumentException("LastName is too short");
    }
}
