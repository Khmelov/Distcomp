using AutoMapper;
using Distcomp.Application.DTOs;
using Distcomp.Application.Exceptions;
using Distcomp.Application.Interfaces;
using Distcomp.Domain.Models;

namespace Distcomp.Application.Services
{
    public class MarkerService : IMarkerService
    {
        private readonly IRepository<Marker> _repository;
        private readonly IMapper _mapper;

        public MarkerService(IRepository<Marker> repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }

        public MarkerResponseTo Create(MarkerRequestTo request)
        {
            ValidateRequest(request);

            if (_repository.GetAll().Any(m => m.Name.Equals(request.Name, StringComparison.OrdinalIgnoreCase)))
                throw new RestException(403, 40302, $"Marker with name '{request.Name}' already exists");

            var marker = _mapper.Map<Marker>(request);
            var created = _repository.Create(marker);
            return _mapper.Map<MarkerResponseTo>(created);
        }

        public MarkerResponseTo? GetById(long id)
        {
            var marker = _repository.GetById(id);
            if (marker == null) throw new RestException(404, 40403, "Marker not found");
            return _mapper.Map<MarkerResponseTo>(marker);
        }

        public IEnumerable<MarkerResponseTo> GetAll() =>
            _mapper.Map<IEnumerable<MarkerResponseTo>>(_repository.GetAll());

        public MarkerResponseTo Update(long id, MarkerRequestTo request)
        {
            var existing = _repository.GetById(id);
            if (existing == null) throw new RestException(404, 40403, "Cannot update: Marker not found");

            ValidateRequest(request);

            if (!existing.Name.Equals(request.Name, StringComparison.OrdinalIgnoreCase) &&
                _repository.GetAll().Any(m => m.Name.Equals(request.Name, StringComparison.OrdinalIgnoreCase)))
                throw new RestException(403, 40302, "New marker name is already taken");

            _mapper.Map(request, existing);
            existing.Id = id;
            _repository.Update(existing);

            return _mapper.Map<MarkerResponseTo>(existing);
        }

        public bool Delete(long id)
        {
            if (_repository.GetById(id) == null) throw new RestException(404, 40403, "Cannot delete: Marker not found");
            return _repository.Delete(id);
        }

        private void ValidateRequest(MarkerRequestTo request)
        {
            if (request.Name.Length < 2 || request.Name.Length > 32)
                throw new RestException(400, 40007, "Marker name must be between 2 and 32 characters");
        }
    }
}