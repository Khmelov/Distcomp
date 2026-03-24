using AutoMapper;
using RestApiTask.Infrastructure.Exceptions;
using RestApiTask.Models.DTOs;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;
using RestApiTask.Services.Interfaces;

namespace RestApiTask.Services;

public class WriterService : IWriterService
{
    private readonly IRepository<Writer> _repo;
    private readonly IMapper _mapper;

    public WriterService(IRepository<Writer> repo, IMapper mapper)
    {
        _repo = repo;
        _mapper = mapper;
    }

    public async Task<IEnumerable<WriterResponseTo>> GetAllAsync() =>
        _mapper.Map<IEnumerable<WriterResponseTo>>(await _repo.GetAllAsync());

    public async Task<WriterResponseTo> GetByIdAsync(long id)
    {
        var entity = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Writer not found");
        return _mapper.Map<WriterResponseTo>(entity);
    }

    public async Task<WriterResponseTo> CreateAsync(WriterRequestTo request)
    {
        Validate(request);
        var entity = _mapper.Map<Writer>(request);
        return _mapper.Map<WriterResponseTo>(await _repo.AddAsync(entity));
    }

    public async Task<WriterResponseTo> UpdateAsync(long id, WriterRequestTo request)
    {
        var existing = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Writer not found");
        Validate(request);
        _mapper.Map(request, existing);
        await _repo.UpdateAsync(existing);
        return _mapper.Map<WriterResponseTo>(existing);
    }

    public async Task DeleteAsync(long id)
    {
        if (!await _repo.DeleteAsync(id)) throw new NotFoundException("Writer not found");
    }

    private void Validate(WriterRequestTo r)
    {
        if (r.Login.Length < 2 || r.Login.Length > 64) throw new ValidationException("Login: 2-64 chars");
        if (r.Password.Length < 8 || r.Password.Length > 128) throw new ValidationException("Password: 8-128 chars");
        if (r.Firstname.Length < 2 || r.Firstname.Length > 64) throw new ValidationException("Firstname: 2-64 chars");
        if (r.Lastname.Length < 2 || r.Lastname.Length > 64) throw new ValidationException("Lastname: 2-64 chars");
    }
}