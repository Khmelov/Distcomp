using Application.DTOs.Requests;
using Application.DTOs.Responses;

namespace Application.Interfaces
{
    public interface IMarkerService
    {
        Task<MarkerResponseTo> CreateMarker(MarkerRequestTo createMarkerRequestTo);

        Task<IEnumerable<MarkerResponseTo>> GetAllMarkers();

        Task<MarkerResponseTo> GetMarker(MarkerRequestTo getMarkerRequestTo);

        Task<MarkerResponseTo> UpdateMarker(MarkerRequestTo updateMarkerRequestTo);

        Task DeleteMarker(MarkerRequestTo deleteMarkerRequestTo);
    }
}
