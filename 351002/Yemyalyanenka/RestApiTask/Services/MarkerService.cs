using AutoMapper;
using RestApiTask.Infrastructure.Exceptions;
using RestApiTask.Models.DTOs;
using RestApiTask.Models.Entities;
using RestApiTask.Repositories;
using RestApiTask.Services.Interfaces;

namespace RestApiTask.Services
{
    public class MarkerService : IMarkerService
    {
        private readonly IRepository<Marker> _repo;
        private readonly IMapper _mapper;

        public MarkerService(IRepository<Marker> repo, IMapper mapper)
        {
            _repo = repo;
            _mapper = mapper;
        }

        public async Task<IEnumerable<MarkerResponseTo>> GetAllAsync() =>
            _mapper.Map<IEnumerable<MarkerResponseTo>>(await _repo.GetAllAsync());

        public async Task<MarkerResponseTo> GetByIdAsync(long id)
        {
            var entity = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Marker not found");
            return _mapper.Map<MarkerResponseTo>(entity);
        }

        public async Task<MarkerResponseTo> CreateAsync(MarkerRequestTo request)
        {
            if (request.Name.Length < 2 || request.Name.Length > 32) throw new ValidationException("Name: 2-32 chars");
            var entity = _mapper.Map<Marker>(request);
            return _mapper.Map<MarkerResponseTo>(await _repo.AddAsync(entity));
        }

        public async Task<MarkerResponseTo> UpdateAsync(long id, MarkerRequestTo request)
        {
            var existing = await _repo.GetByIdAsync(id) ?? throw new NotFoundException("Marker not found");
            if (request.Name.Length < 2 || request.Name.Length > 32) throw new ValidationException("Name: 2-32 chars");
            _mapper.Map(request, existing);
            await _repo.UpdateAsync(existing);
            return _mapper.Map<MarkerResponseTo>(existing);
        }

        public async Task DeleteAsync(long id)
        {
            if (!await _repo.DeleteAsync(id)) throw new NotFoundException("Marker not found");
        }
    }
}
