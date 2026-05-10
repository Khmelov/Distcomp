using AutoMapper;
using Task310RestApi.DTOs.Request;
using Task310RestApi.DTOs.Response;
using Task310RestApi.Exceptions;
using Task310RestApi.Interfaces;
using Task310RestApi.Models;
using Task310RestApi.Repositories; // Измените пространство имен

namespace Task310RestApi.Services // Измените на Task310RestApi.Services
{
    public class PostService : IPostService
    {
        private readonly IRepository<Post> _postRepository;
        private readonly IRepository<News> _newsRepository;
        private readonly IMapper _mapper;

        public PostService(
            IRepository<Post> postRepository,
            IRepository<News> newsRepository,
            IMapper mapper)
        {
            _postRepository = postRepository;
            _newsRepository = newsRepository;
            _mapper = mapper;
        }

        public async Task<IEnumerable<PostResponseTo>> GetAllPostsAsync()
        {
            var posts = await _postRepository.GetAllAsync();
            return _mapper.Map<IEnumerable<PostResponseTo>>(posts);
        }

        public async Task<PostResponseTo?> GetPostByIdAsync(long id)
        {
            var post = await _postRepository.GetByIdAsync(id);
            if (post == null)
            {
                throw new ResourceNotFoundException($"Post not found with id: {id}");
            }
            return _mapper.Map<PostResponseTo>(post);
        }

        public async Task<PostResponseTo> CreatePostAsync(PostRequestTo postRequest)
        {
            ValidatePostRequest(postRequest);
            
            // Проверяем существование новости
            if (!await _newsRepository.ExistsAsync(postRequest.NewsId))
            {
                throw new ValidationException($"News not found with id: {postRequest.NewsId}", "40005");
            }

            var post = _mapper.Map<Post>(postRequest);
            post.Created = DateTime.UtcNow;
            post.Modified = DateTime.UtcNow;
            
            var createdPost = await _postRepository.CreateAsync(post);
            return _mapper.Map<PostResponseTo>(createdPost);
        }

        public async Task<PostResponseTo?> UpdatePostAsync(long id, PostRequestTo postRequest)
        {
            ValidatePostRequest(postRequest);
            
            var existingPost = await _postRepository.GetByIdAsync(id);
            if (existingPost == null)
            {
                throw new ResourceNotFoundException($"Post not found with id: {id}");
            }

            // Проверяем существование новости
            if (!await _newsRepository.ExistsAsync(postRequest.NewsId))
            {
                throw new ValidationException($"News not found with id: {postRequest.NewsId}", "40005");
            }

            _mapper.Map(postRequest, existingPost);
            existingPost.Modified = DateTime.UtcNow;
            
            var updatedPost = await _postRepository.UpdateAsync(existingPost);
            return _mapper.Map<PostResponseTo>(updatedPost);
        }

        public async Task<bool> DeletePostAsync(long id)
        {
            if (!await _postRepository.ExistsAsync(id))
            {
                throw new ResourceNotFoundException($"Post not found with id: {id}");
            }

            return await _postRepository.DeleteAsync(id);
        }

        public async Task<bool> ExistsAsync(long id)
        {
            return await _postRepository.ExistsAsync(id);
        }

        public async Task<IEnumerable<PostResponseTo>> GetPostsByNewsIdAsync(long newsId)
        {
            if (!await _newsRepository.ExistsAsync(newsId))
            {
                throw new ResourceNotFoundException($"News not found with id: {newsId}");
            }

            // Временное решение до создания репозиториев
            var allPosts = await _postRepository.GetAllAsync();
            var posts = allPosts.Where(p => p.NewsId == newsId);
            return _mapper.Map<IEnumerable<PostResponseTo>>(posts);
        }

        private void ValidatePostRequest(PostRequestTo request)
        {
            var validationResults = new List<System.ComponentModel.DataAnnotations.ValidationResult>();
            var validationContext = new System.ComponentModel.DataAnnotations.ValidationContext(request);
            
            if (!System.ComponentModel.DataAnnotations.Validator.TryValidateObject(request, validationContext, validationResults, true))
            {
                var errorMessages = string.Join("; ", validationResults.Select(r => r.ErrorMessage));
                throw new ValidationException(errorMessages, "40000");
            }
        }
    }
}