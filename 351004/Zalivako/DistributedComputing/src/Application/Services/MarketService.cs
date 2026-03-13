using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Interfaces;
using AutoMapper;
using Core.Entities;

namespace Application.Services
{
    public class MarkerService : IMarkerService
    {
        private readonly IMapper _mapper;

        private readonly IMarkerRepository _markerRepository;

        public MarkerService(IMapper mapper, IMarkerRepository repository)
        {
            _mapper = mapper;
            _markerRepository = repository;
        }

        public async Task<MarkerResponseTo> CreateMarker(MarkerRequestTo createMarkerRequestTo)
        {
            Marker markerFromDto = _mapper.Map<Marker>(createMarkerRequestTo);

            Marker createdMarker = await _markerRepository.AddAsync(markerFromDto);

            MarkerResponseTo dtoFromCreatedMarker = _mapper.Map<MarkerResponseTo>(createdMarker);

            return dtoFromCreatedMarker;
        }

        public async Task DeleteMarker(MarkerRequestTo deleteMarkerRequestTo)
        {
            Marker markerFromDto = _mapper.Map<Marker>(deleteMarkerRequestTo);

            _ = await _markerRepository.DeleteAsync(markerFromDto)
                ?? throw new MarkerNotFoundException($"Delete marker {markerFromDto} was not found");
        }

        public async Task<IEnumerable<MarkerResponseTo>> GetAllMarkers()
        {
            IEnumerable<Marker> allMarkers = await _markerRepository.GetAllAsync();

            var allMarkersResponseTos = new List<MarkerResponseTo>();
            foreach (Marker marker in allMarkers)
            {
                MarkerResponseTo markerTo = _mapper.Map<MarkerResponseTo>(marker);
                allMarkersResponseTos.Add(markerTo);
            }

            return allMarkersResponseTos;
        }

        public async Task<MarkerResponseTo> GetMarker(MarkerRequestTo getMarkersRequestTo)
        {
            Marker markerFromDto = _mapper.Map<Marker>(getMarkersRequestTo);

            Marker demandedMarker = await _markerRepository.GetByIdAsync(markerFromDto.Id)
                ?? throw new MarkerNotFoundException($"Demanded marker {markerFromDto} was not found");

            MarkerResponseTo demandedMarkerResponseTo = _mapper.Map<MarkerResponseTo>(demandedMarker);

            return demandedMarkerResponseTo;
        }

        public async Task<MarkerResponseTo> UpdateMarker(MarkerRequestTo updateMarkerRequestTo)
        {
            Marker markerFromDto = _mapper.Map<Marker>(updateMarkerRequestTo);

            Marker updateMarker = await _markerRepository.UpdateAsync(markerFromDto)
                ?? throw new MarkerNotFoundException($"Update marker {markerFromDto} was not found");

            MarkerResponseTo updateMarkerResponseTo = _mapper.Map<MarkerResponseTo>(updateMarker);

            return updateMarkerResponseTo;
        }
    }
}
