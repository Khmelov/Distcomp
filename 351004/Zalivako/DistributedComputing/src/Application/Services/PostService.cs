using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Interfaces;
using AutoMapper;
using Core.Entities;

namespace Application.Services
{
    public class PostService : IPostService
    {
        private readonly IMapper _mapper;

        private readonly IPostRepository _postRepository;

        public PostService(IMapper mapper, IPostRepository repository)
        {
            _mapper = mapper;
            _postRepository = repository;
        }

        public async Task<PostResponseTo> CreatePost(PostRequestTo createPostRequestTo)
        {
            Post postFromDto = _mapper.Map<Post>(createPostRequestTo);

            Post createdPost = await _postRepository.AddAsync(postFromDto);

            PostResponseTo dtoFromCreatedPost = _mapper.Map<PostResponseTo>(createdPost);

            return dtoFromCreatedPost;
        }

        public async Task DeletePost(PostRequestTo deletePostRequestTo)
        {
            Post postFromDto = _mapper.Map<Post>(deletePostRequestTo);

            _ = await _postRepository.DeleteAsync(postFromDto)
                ?? throw new PostNotFoundException($"Delete post {postFromDto} was not found");
        }

        public async Task<IEnumerable<PostResponseTo>> GetAllPosts()
        {
            IEnumerable<Post> allPosts = await _postRepository.GetAllAsync();

            var allPostsResponseTos = new List<PostResponseTo>();
            foreach (Post post in allPosts)
            {
                PostResponseTo postTo = _mapper.Map<PostResponseTo>(post);
                allPostsResponseTos.Add(postTo);
            }

            return allPostsResponseTos;
        }

        public async Task<PostResponseTo> GetPost(PostRequestTo getPostsRequestTo)
        {
            Post postFromDto = _mapper.Map<Post>(getPostsRequestTo);

            Post demandedPost = await _postRepository.GetByIdAsync(postFromDto.Id)
                ?? throw new PostNotFoundException($"Demanded post {postFromDto} was not found");

            PostResponseTo demandedPostResponseTo = _mapper.Map<PostResponseTo>(demandedPost);

            return demandedPostResponseTo;
        }

        public async Task<PostResponseTo> UpdatePost(PostRequestTo updatePostRequestTo)
        {
            Post postFromDto = _mapper.Map<Post>(updatePostRequestTo);

            Post updatePost = await _postRepository.UpdateAsync(postFromDto)
                ?? throw new PostNotFoundException($"Update post {postFromDto} was not found");

            PostResponseTo updatePostResponseTo = _mapper.Map<PostResponseTo>(updatePost);

            return updatePostResponseTo;
        }
    }
}
