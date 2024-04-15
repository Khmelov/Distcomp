using Publisher.Entity.Db;
using Publisher.Entity.DTO.RequestTO;
using Publisher.Entity.DTO.ResponseTO;
using Publisher.Service.Interface.Common;

namespace Publisher.Service.Interface
{
    public interface IMarkerService : ICrudService<Marker, MarkerRequestTO, MarkerResponseTO>
    {
        Task<IList<MarkerResponseTO>> GetByTweetID(int tweetId);
    }
}
