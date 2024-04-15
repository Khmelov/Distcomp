using AutoMapper;
using Publisher.Entity.Db;
using Publisher.Entity.DTO.RequestTO;
using Publisher.Entity.DTO.ResponseTO;
using Publisher.Repository.Interface;
using Publisher.Service.Implementation.Common;
using Publisher.Service.Interface;
using Publisher.Storage.Common;

namespace Publisher.Service.Implementation
{
    public class TweetService(IMapper mapper, ITweetRepository repository)
        : AbstractCrudService<Tweet, TweetRequestTO, TweetResponseTO>(mapper, repository), ITweetService
    {
        public override async Task<TweetResponseTO> Add(TweetRequestTO tweet)
        {
            if (!Validate(tweet))
            {
                throw new InvalidDataException("TWEET is not valid");
            }

            return await base.Add(tweet);
        }

        public override async Task<TweetResponseTO> Update(TweetRequestTO tweet)
        {
            if (!Validate(tweet))
            {
                throw new InvalidDataException($"UPDATE invalid data: {tweet}");
            }

            return await base.Update(tweet);
        }

        public Task<TweetResponseTO> GetTweetByParam(IList<string> markerNames, IList<int> markerIds, string authorLogin,
            string title, string content)
        {
            throw new NotImplementedException();
        }

        private static bool Validate(TweetRequestTO tweet)
        {
            var titleLen = tweet.Title.Length;
            var contentLen = tweet.Content.Length;

            if (titleLen < 2 || titleLen > 64)
            {
                return false;
            }
            if (contentLen < 4 || contentLen > 2048)
            {
                return false;
            }
            if (tweet.Modified < tweet.Created)
            {
                return false;
            }
            return true;
        }
    }
}
