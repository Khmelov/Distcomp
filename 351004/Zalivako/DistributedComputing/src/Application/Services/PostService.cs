using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Interfaces;
using AutoMapper;
using Core.Entities;
using Microsoft.Extensions.Caching.Distributed;
using System.Text.Json;

namespace Application.Services
{
    public class PostService : IPostService
    {
        private readonly IMapper _mapper;
        private readonly IPostRepository _postRepository;
        private readonly IKafkaProducer _kafkaProducer;
        private readonly IDistributedCache _cache;

        private const string AllPostsCacheKey = "posts_all";
        private static string PostByIdKey(long id) => $"post_{id}";

        public PostService(IMapper mapper, IPostRepository postRepository,
                           IKafkaProducer kafkaProducer, IDistributedCache cache)
        {
            _mapper = mapper;
            _postRepository = postRepository;
            _kafkaProducer = kafkaProducer;
            _cache = cache;
        }

        public async Task<PostResponseTo> CreatePost(PostRequestTo request)
        {
            var post = _mapper.Map<Post>(request);
            post.State = PostState.PENDING;
            var created = await _postRepository.AddAsync(post);
            var response = _mapper.Map<PostResponseTo>(created);

            await _kafkaProducer.SendPostAsync(created);
            await InvalidateCacheAsync(created.Id);

            return response;
        }

        public async Task<PostResponseTo?> UpdatePost(PostRequestTo request)
        {
            var post = _mapper.Map<Post>(request);
            var existing = await _postRepository.GetByIdAsync(post.Id);
            if (existing == null) return null;
            post.State = existing.State;   // сохраняем текущий статус

            var updated = await _postRepository.UpdateAsync(post);
            if (updated == null) return null;

            var response = _mapper.Map<PostResponseTo>(updated);
            await _kafkaProducer.SendPostAsync(updated);
            await InvalidateCacheAsync(updated.Id);

            return response;
        }

        public async Task<IEnumerable<PostResponseTo>> GetAllPosts()
        {
            var cached = await _cache.GetStringAsync(AllPostsCacheKey);
            if (!string.IsNullOrEmpty(cached))
                return JsonSerializer.Deserialize<List<PostResponseTo>>(cached)!;

            var posts = await _postRepository.GetAllAsync();
            var result = _mapper.Map<List<PostResponseTo>>(posts);

            var options = new DistributedCacheEntryOptions()
                .SetAbsoluteExpiration(TimeSpan.FromMinutes(5));
            await _cache.SetStringAsync(AllPostsCacheKey, JsonSerializer.Serialize(result), options);
            return result;
        }

        public async Task<PostResponseTo> GetPost(PostRequestTo getPostRequestTo)
        {
            long id = getPostRequestTo.Id ?? 0;
            string cacheKey = PostByIdKey(id);
            var cached = await _cache.GetStringAsync(cacheKey);
            if (!string.IsNullOrEmpty(cached))
                return JsonSerializer.Deserialize<PostResponseTo>(cached)!;

            var post = await _postRepository.GetByIdAsync(id);
            if (post == null) return null;

            var response = _mapper.Map<PostResponseTo>(post);
            var options = new DistributedCacheEntryOptions()
                .SetAbsoluteExpiration(TimeSpan.FromMinutes(5));
            await _cache.SetStringAsync(cacheKey, JsonSerializer.Serialize(response), options);
            return response;
        }

        public async Task DeletePost(PostRequestTo deletePostRequestTo)
        {
            var post = _mapper.Map<Post>(deletePostRequestTo);
            await _postRepository.DeleteAsync(post);
            await InvalidateCacheAsync(post.Id);
        }

        // Публичный метод, чтобы Kafka-консьюмер мог сбросить кэш
        public async Task InvalidateCacheAsync(long postId)
        {
            await _cache.RemoveAsync(AllPostsCacheKey);
            await _cache.RemoveAsync(PostByIdKey(postId));
        }
    }
}