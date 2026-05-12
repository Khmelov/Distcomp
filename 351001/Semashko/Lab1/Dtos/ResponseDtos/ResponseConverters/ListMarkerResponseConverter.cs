using lab_1.Domain;

namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public class ListMarkerResponseConverter
    {
        private MarkerResponseConverter markerResponseConverter = new MarkerResponseConverter();
        public IEnumerable<MarkerResponseDto> MarkersResponse(IEnumerable<Marker> list)
        {
            foreach (Marker node in list)
                yield return markerResponseConverter.ToDto(node);
        }
    }
}
