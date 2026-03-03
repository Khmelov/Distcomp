using AutoMapper;
using Project.Dto;
using Project.Exceptions;
using Project.Model;
using Project.Repository;

namespace Project.Service {
    public class TweetService : BaseService<Tweet, TweetRequestTo, TweetResponseTo> {
        private readonly IRepository<Tweet> _tweetRepository;
        private readonly IRepository<Editor> _editorRepository;

        public TweetService(
            IRepository<Tweet> repository,
            IMapper mapper,
            ILogger<TweetService> logger,
            IRepository<Editor> editorRepository)
            : base(repository, mapper, logger) {
            _tweetRepository = repository;
            _editorRepository = editorRepository;
        }

        public async Task<TweetResponseTo?> CreateTweetAsync(TweetRequestTo request) {
            // Проверяем существование редактора
            var editor = await _editorRepository.GetByIdAsync(request.EditorId);
            if (editor == null)
                throw new ValidationException($"Editor with id {request.EditorId} not found");

            var tweet = _mapper.Map<Tweet>(request);
            tweet.Created = DateTime.UtcNow;
            tweet.Modified = DateTime.UtcNow;

            var createdTweet = await _tweetRepository.AddAsync(tweet);
            return _mapper.Map<TweetResponseTo>(createdTweet);
        }

        public async Task<TweetResponseTo?> UpdateTweetAsync(TweetRequestTo request) {
            var existingTweet = await _tweetRepository.GetByIdAsync(request.Id);
            if (existingTweet == null)
                return null;

            // Проверяем существование редактора
            var editor = await _editorRepository.GetByIdAsync(request.EditorId);
            if (editor == null)
                throw new ValidationException($"Editor with id {request.EditorId} not found");

            var tweet = _mapper.Map<Tweet>(request);
            tweet.Created = existingTweet.Created;
            tweet.Modified = DateTime.UtcNow;

            var updatedTweet = await _tweetRepository.UpdateAsync(tweet);
            return _mapper.Map<TweetResponseTo>(updatedTweet);
        }
    }
}