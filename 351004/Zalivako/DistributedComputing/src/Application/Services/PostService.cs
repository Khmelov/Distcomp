using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Exceptions.Application;
using Application.Exceptions.Persistance;
using Application.Interfaces;
using AutoMapper;
using Core.Entities;

namespace Application.Services
{
    public class PostService : IPostService
    {
        private readonly IMapper _mapper;

        private readonly IPostRepository _postRepository;
        private readonly IKafkaProducer _kafkaProducer;   // будет добавлен позже

        public PostService(IMapper mapper, IPostRepository postRepository, IKafkaProducer kafkaProducer)
        {
            _mapper = mapper;
            _postRepository = postRepository;
            _kafkaProducer = kafkaProducer;
        }

        public async Task<PostResponseTo> CreatePost(PostRequestTo request)
        {
            var post = _mapper.Map<Post>(request);
            post.State = PostState.PENDING;

            var created = await _postRepository.AddAsync(post);
            var response = _mapper.Map<PostResponseTo>(created);

            // Отправка в Kafka
            await _kafkaProducer.SendPostAsync(created);

            return response;
        }

        public async Task<PostResponseTo?> UpdatePost(PostRequestTo request)
        {
            var post = _mapper.Map<Post>(request);
            // Важно: сохранить текущий State, если он уже был изменён (например, через Kafka)
            var existing = await _postRepository.GetByIdAsync(post.Id);
            if (existing == null) return null;
            post.State = existing.State;   // не сбрасываем статус при обновлении контента

            var updated = await _postRepository.UpdateAsync(post);
            if (updated == null) return null;

            var response = _mapper.Map<PostResponseTo>(updated);

            // Отправка в Kafka при обновлении
            await _kafkaProducer.SendPostAsync(updated);

            return response;
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
    }
}
