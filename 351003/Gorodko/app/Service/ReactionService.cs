// ReactionService.cs
using AutoMapper;
using Project.Dto;
using Project.Exceptions;
using Project.Model;
using Project.Repository;

namespace Project.Service {
    public class ReactionService : BaseService<Reaction, ReactionRequestTo, ReactionResponseTo> {
        private readonly IRepository<Tweet> _tweetRepository;

        public ReactionService(
            IRepository<Reaction> repository,
            IMapper mapper,
            ILogger<ReactionService> logger,
            IRepository<Tweet> tweetRepository)
            : base(repository, mapper, logger) {
            _tweetRepository = tweetRepository;
        }

        public async Task<ReactionResponseTo?> CreateReactionAsync(ReactionRequestTo request) {
            // Проверяем существование твита
            var tweet = await _tweetRepository.GetByIdAsync(request.TweetId);
            if (tweet == null)
                throw new ValidationException($"Tweet with id {request.TweetId} not found");

            return await AddAsync(request);
        }

        public async Task<ReactionResponseTo?> UpdateReactionAsync(ReactionRequestTo request) {
            // Проверяем существование твита
            var tweet = await _tweetRepository.GetByIdAsync(request.TweetId);
            if (tweet == null)
                throw new ValidationException($"Tweet with id {request.TweetId} not found");

            return await UpdateAsync(request);
        }
    }
}