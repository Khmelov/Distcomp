using AutoMapper;
using BusinessLogic.DTO.Request;
using BusinessLogic.DTO.Response;
using BusinessLogic.Repository;
using BusinessLogic.Servicies;
using DataAccess.Models;

namespace Infrastructure.ServiceImplementation
{
    public class PostService : IBaseService<PostRequestTo, PostResponseTo>
    {
        protected readonly IRepository<Post> _postRepository;
        protected readonly IMapper _mapper;

        public PostService(
            IRepository<Post> postRepository,
            IMapper mapper)
        {
            _postRepository = postRepository;
            _mapper = mapper;
        }

        public virtual async Task<List<PostResponseTo>> GetAllAsync()
        {
            var posts = await _postRepository.GetAllAsync();
            return _mapper.Map<List<PostResponseTo>>(posts);
        }

        public virtual async Task<PostResponseTo?> GetByIdAsync(int id)
        {
            if (await _postRepository.ExistsAsync(id))
            {
                var post = await _postRepository.GetByIdAsync(id);
                return _mapper.Map<PostResponseTo>(post);
            }
            return null;
        }

        public virtual async Task<bool> DeleteByIdAsync(int id)
        {
            if (await _postRepository.ExistsAsync(id))
            {
                await _postRepository.DeleteAsync(id);
                return true;
            }
            return false;
        }

        public virtual async Task<PostResponseTo> CreateAsync(PostRequestTo entity)
        {
            var post = _mapper.Map<Post>(entity);
            post.Id = entity.Id;
            Console.WriteLine($"[Discussion Service] Saving Post to DB. ID: {post.Id}, Content: {post.Content}");
            await _postRepository.CreateAsync(post);
            return _mapper.Map<PostResponseTo>(post);
        }

        public virtual async Task<PostResponseTo?> UpdateAsync(PostRequestTo entity)
        {
            var existingPost = await _postRepository.GetByIdAsync(entity.Id);
            if (existingPost == null)
            {
                return null;
            }

            existingPost.Content = entity.Content;
            existingPost.StoryId = entity.StoryId;
            await _postRepository.UpdateAsync(existingPost);
            return _mapper.Map<PostResponseTo>(existingPost);
        }
    }
}