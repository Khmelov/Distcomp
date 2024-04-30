using AutoMapper;
using RV.Models;
using RV.Repositories;
using RV.Views;
using RV.Views.DTO;

namespace RV.Services.DataProviderServices.SQL
{
    public class SQLTweetDataProvider : ITweetDataProvider
    {
        private IRepository<Tweet> _repository;
        private IMapper _mapper;

        public SQLTweetDataProvider(IRepository<Tweet> repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }

        public TweetDTO CreateTweet(TweetAddDTO item)
        {
            item.created = DateTime.UtcNow;
            item.modified = DateTime.UtcNow;
            Tweet n = _mapper.Map<Tweet>(item);
            var res = _repository.Create(n);
            return _mapper.Map<TweetDTO>(res);
        }

        public int DeleteTweet(int id)
        {
            int res = _repository.Delete(id);
            return res;
        }

        public TweetDTO GetTweet(int id)
        {
            return _mapper.Map<TweetDTO>(_repository.Get(id));
        }

        public List<TweetDTO> GetTweets()
        {
            List<TweetDTO> res = [];
            foreach (Tweet n in _repository.GetAll())
            {
                res.Add(_mapper.Map<TweetDTO>(n));
            }
            return res;
        }

        public TweetDTO UpdateTweet(TweetUpdateDTO item)
        {
            var n = _mapper.Map<Tweet>(item);
            var res = _repository.Update(n);
            return _mapper.Map<TweetDTO>(res);
        }
    }
}
