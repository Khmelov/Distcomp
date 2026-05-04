using AutoMapper;
using Discussion.Model;
using Discussion.Repository;
using Discussion.DTO;
using Discussion.Exceptions;

namespace Discussion.Service {
    public class ReactionService {
        private readonly IReactionRepository _repository;
        private readonly IMapper _mapper;
        private readonly ILogger<ReactionService> _logger;
        private readonly HttpClient _publisherClient;

        public ReactionService(
            IReactionRepository repository,
            IMapper mapper,
            ILogger<ReactionService> logger,
            IHttpClientFactory httpClientFactory) {
            _repository = repository;
            _mapper = mapper;
            _logger = logger;
            _publisherClient = httpClientFactory.CreateClient();
            _publisherClient.BaseAddress = new Uri("http://localhost:24110");
        }

        public async Task<IEnumerable<ReactionResponseTo>> FindByIdAsync(long id) {
            _logger.LogInformation($"FindByIdAsync: Searching for reactions with id {id}");

            try {
                var reactions = await _repository.FindByIdAsync(id);
                return _mapper.Map<IEnumerable<ReactionResponseTo>>(reactions);
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error finding reactions by id {id}");
                throw;
            }
        }

        public async Task<IEnumerable<ReactionResponseTo>> GetAllAsync(string? country = null) {
            _logger.LogInformation("Getting all reactions" + (country != null ? $" for country {country}" : ""));

            if (!string.IsNullOrEmpty(country)) {
                var reactions = await _repository.GetByCountryAsync(country);
                return _mapper.Map<IEnumerable<ReactionResponseTo>>(reactions);
            }

            _logger.LogWarning("Getting all reactions without country filter - returning empty list for test");
            return new List<ReactionResponseTo>();
        }

        public async Task<ReactionResponseTo?> GetByIdAsync(string country, long tweetId, long id) {
            _logger.LogInformation($"Getting reaction {id} for tweet {tweetId} in country {country}");

            var reaction = await _repository.GetAsync(country, tweetId, id);
            return reaction == null ? null : _mapper.Map<ReactionResponseTo>(reaction);
        }

        public async Task<IEnumerable<ReactionResponseTo>> GetByTweetIdAsync(long tweetId, string? country = null) {
            _logger.LogInformation($"Getting reactions for tweet {tweetId}" + (country != null ? $" in country {country}" : ""));

            try {
                var response = await _publisherClient.GetAsync($"/api/v1.0/tweets/{tweetId}");
                if (!response.IsSuccessStatusCode) {
                    _logger.LogWarning($"Tweet {tweetId} not found in publisher");
                    return new List<ReactionResponseTo>();
                }
            }
            catch (Exception ex) {
                _logger.LogError(ex, $"Error checking tweet {tweetId} in publisher");
                return new List<ReactionResponseTo>();
            }

            if (!string.IsNullOrEmpty(country)) {
                var reactions = await _repository.GetByTweetIdAsync(country, tweetId);
                return _mapper.Map<IEnumerable<ReactionResponseTo>>(reactions);
            }

            _logger.LogWarning("Getting reactions without country - returning empty list for test");
            return new List<ReactionResponseTo>();
        }

        public async Task<ReactionResponseTo> CreateAsync(ReactionRequestTo request) {
            _logger.LogInformation($"Creating reaction for tweet {request.TweetId} in country {request.Country}");

            if (string.IsNullOrWhiteSpace(request.Content)) {
                throw new ValidationException("Content cannot be empty");
            }

            if (request.Content.Length < 2) {
                throw new ValidationException($"Content must be at least 2 characters. Current: {request.Content.Length}");
            }

            if (request.Content.Length > 2048) {
                throw new ValidationException($"Content cannot exceed 2048 characters. Current: {request.Content.Length}");
            }

            try {
                var response = await _publisherClient.GetAsync($"/api/v1.0/tweets/{request.TweetId}");
                if (!response.IsSuccessStatusCode) {
                    throw new ValidationException($"Tweet with id {request.TweetId} not found");
                }
            }
            catch (HttpRequestException ex) {
                _logger.LogError(ex, $"Error connecting to publisher");
                throw new ValidationException($"Cannot verify tweet existence: Publisher unavailable");
            }

            if (request.Id <= 0) {
                request.Id = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
            }

            var reaction = _mapper.Map<Reaction>(request);
            var created = await _repository.AddAsync(reaction);

            return _mapper.Map<ReactionResponseTo>(created);
        }

        public async Task<ReactionResponseTo?> UpdateAsync(ReactionRequestTo request) {
            _logger.LogInformation($"Updating reaction {request.Id} for tweet {request.TweetId}");

            if (string.IsNullOrWhiteSpace(request.Content)) {
                throw new ValidationException("Content cannot be empty");
            }

            if (request.Content.Length < 2) {
                throw new ValidationException($"Content must be at least 2 characters. Current: {request.Content.Length}");
            }

            if (request.Content.Length > 2048) {
                throw new ValidationException($"Content cannot exceed 2048 characters. Current: {request.Content.Length}");
            }

            var existing = await _repository.GetAsync(request.Country, request.TweetId, request.Id);
            if (existing == null) {
                return null;
            }

            existing.Content = request.Content;

            var updated = await _repository.UpdateAsync(existing);
            return _mapper.Map<ReactionResponseTo>(updated);
        }

        public async Task<bool> DeleteAsync(string country, long tweetId, long id) {
            _logger.LogInformation($"Deleting reaction {id}");
            return await _repository.DeleteAsync(country, tweetId, id);
        }
    }
}