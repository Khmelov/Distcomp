using AutoMapper;
using DiscussionService.DTOs.Requests;
using DiscussionService.DTOs.Responses;
using DiscussionService.Interfaces;
using DiscussionService.Models;

namespace DiscussionService.Services
{
    public class PostService : IPostService
    {
        private readonly IMapper _mapper;

        private readonly IPostRepository _repository;

        public PostService(IMapper mapper, IPostRepository repository)
        {
            _mapper = mapper;
            _repository = repository;
        }

        public async Task<PostResponseTo> CreatePost(PostRequestTo createPostRequestTo)
        {
            Post postFromDto = _mapper.Map<Post>(createPostRequestTo);

            Post createdPost = await _repository.AddAsync(postFromDto);

            PostResponseTo dtoFromCreatedPost = _mapper.Map<PostResponseTo>(createdPost);

            return dtoFromCreatedPost;
        }

        public async Task<IEnumerable<PostResponseTo>> GetAllPosts()
        {
            IEnumerable<Post> allPosts = await _repository.GetAllAsync();

            var allPostsResponseTos = new List<PostResponseTo>();

            foreach (Post post in allPosts)
            {
                PostResponseTo postTo = _mapper.Map<PostResponseTo>(post);
                allPostsResponseTos.Add(postTo);
            }

            return allPostsResponseTos;
        }

        public async Task<PostResponseTo?> GetPost(PostRequestTo getPostsRequestTo)
        {
            Post postFromDto = _mapper.Map<Post>(getPostsRequestTo);

            Post demandedPost = await _repository.GetByIdAsync(postFromDto.Id)
                ?? throw new ArgumentException($"Update post {postFromDto} was not found");

            PostResponseTo demandedPostResponseTo = _mapper.Map<PostResponseTo>(demandedPost);

            return demandedPostResponseTo;
        }

        public async Task<PostResponseTo?> UpdatePost(PostRequestTo updatePostRequestTo)
        {
            Post postFromDto = _mapper.Map<Post>(updatePostRequestTo);

            Post updatePost = await _repository.UpdateAsync(postFromDto)
                ?? throw new ArgumentException($"Update post {postFromDto} was not found");

            PostResponseTo updatePostResponseTo = _mapper.Map<PostResponseTo>(updatePost);

            return updatePostResponseTo;
        }

        public async Task DeletePost(PostRequestTo deletePostRequestTo)
        {
            Post postFromDto = _mapper.Map<Post>(deletePostRequestTo);

            await _repository.DeleteAsync(postFromDto);
        }

        public async Task<Post> CreatePostInternalAsync(Post post)
        {
            // Предполагаем, что post.Id уже установлен (приходит из publisher)
            // Проверяем, нет ли уже такого поста
            var existing = await _repository.GetByIdAsync(post.Id);
            if (existing != null)
            {
                // Если уже есть, лучше обновить, но по логике задания мы просто обновим.
                return await UpdatePostInternalAsync(post);
            }

            return await _repository.AddAsync(post);
        }

        public async Task<Post> UpdatePostInternalAsync(Post post)
        {
            var updated = await _repository.UpdateAsync(post);
            if (updated == null)
            {
                // Если запись не найдена, создаём новую
                return await _repository.AddAsync(post);
            }
            return updated;
        }

        public async Task UpdatePostStateAsync(long postId, PostState state)
        {
            var post = await _repository.GetByIdAsync(postId);
            if (post == null)
                throw new ArgumentException($"Post with id {postId} not found");

            post.State = state;
            await _repository.UpdateAsync(post);
        }

        public async Task<Post?> GetPostById(long id)
        {
            return await _repository.GetByIdAsync(id);
        }
    }

}
