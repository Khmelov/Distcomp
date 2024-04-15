using AutoMapper;
using Publisher.Entity.Db;
using Publisher.Entity.DTO.RequestTO;
using Publisher.Entity.DTO.ResponseTO;
using Publisher.Repository.Interface;
using Publisher.Service.Implementation.Common;
using Publisher.Service.Interface;

namespace Publisher.Service.Implementation
{
    public class MarkerService(IMapper mapper, IMarkerRepository repository)
        : AbstractCrudService<Marker, MarkerRequestTO, MarkerResponseTO>(mapper, repository), IMarkerService
    {
        public override async Task<MarkerResponseTO> Add(MarkerRequestTO marker)
        {
            if (!Validate(marker))
            {
                throw new InvalidDataException("MARKER is not valid");
            }

            return await base.Add(marker);
        }

        public override async Task<MarkerResponseTO> Update(MarkerRequestTO marker)
        {
            if (!Validate(marker))
            {
                throw new InvalidDataException($"UPDATE invalid data: {marker}");
            }

            return await base.Update(marker);
        }

        public Task<IList<MarkerResponseTO>> GetByTweetID(int tweetId)
        {
            throw new NotImplementedException();
        }

        private static bool Validate(MarkerRequestTO marker)
        {
            var nameLen = marker.Name.Length;

            if (nameLen < 2 || nameLen > 32)
            {
                return false;
            }
            return true;
        }
    }
}
