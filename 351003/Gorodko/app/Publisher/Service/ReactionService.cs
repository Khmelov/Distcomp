// ReactionService.cs
using AutoMapper;
using Publisher.Dto;
using Publisher.Exceptions;
using Publisher.Model;
using Publisher.Repository;

namespace Publisher.Service {
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

            // 1. ВАЛИДАЦИЯ: проверяем длину content (2-2048 символов)
            if (string.IsNullOrWhiteSpace(request.Content)) {
                throw new ValidationException("Content cannot be empty");
            }

            if (request.Content.Length < 2) {
                throw new ValidationException($"Content must be at least 2 characters long. Current length: {request.Content.Length}");
            }

            if (request.Content.Length > 2048) {
                throw new ValidationException($"Content must not exceed 2048 characters. Current length: {request.Content.Length}");
            }

            return await AddAsync(request);
        }

        public async Task<ReactionResponseTo?> UpdateReactionAsync(ReactionRequestTo request) {
            // Проверяем существование твита
            var tweet = await _tweetRepository.GetByIdAsync(request.TweetId);
            if (tweet == null)
                throw new ValidationException($"Tweet with id {request.TweetId} not found");

            // 1. ВАЛИДАЦИЯ: проверяем длину content (2-2048 символов)
            if (string.IsNullOrWhiteSpace(request.Content)) {
                throw new ValidationException("Content cannot be empty");
            }

            if (request.Content.Length < 2) {
                throw new ValidationException($"Content must be at least 2 characters long. Current length: {request.Content.Length}");
            }

            if (request.Content.Length > 2048) {
                throw new ValidationException($"Content must not exceed 2048 characters. Current length: {request.Content.Length}");
            }

            return await UpdateAsync(request);
        }
    }
}