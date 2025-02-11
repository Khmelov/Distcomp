using Application.DTO.Request.Post;
using Application.DTO.Response.Post;
using Application.Interfaces;
using AutoMapper;
using Domain.Entities;
using Domain.Exceptions;
using Domain.Repository;

namespace Application.Services;

public class PostService (IPostRepository _postRepository,INewsRepository _newsRepository, IMapper _mapper) : IPostService
{
    public async Task<PostResponseToGetById?> CreatePost(PostRequestToCreate model)
    {
        Post post = _mapper.Map<Post>(model);
        Validate(post);
        post = await _postRepository.AddPost(post);
        return _mapper.Map<PostResponseToGetById>(post);
    }

    public async Task<IEnumerable<PostResponseToGetById?>?> GetPosts(PostRequestToGetAll request)
    {
        var post = await _postRepository.GetAllPosts();
        return post.Select(_mapper.Map<PostResponseToGetById>);
    }

    public async Task<IEnumerable<PostResponseToGetById?>?> GetPostsByNewsId(PostRequestToGetByNewsId request)
    {
        var posts = await _postRepository.GetPostsByNewsId(request.NewsId);
        return posts.Select(_mapper.Map<PostResponseToGetById>);
    }

    public async Task<PostResponseToGetById?> GetPostById(PostRequestToGetById request)
    {
        var post = await _postRepository.GetPost(request.Id);
        return _mapper.Map<PostResponseToGetById>(post);
    }

    public async Task<PostResponseToGetById?> UpdatePost(PostRequestToFullUpdate model)
    {
        var post = _mapper.Map<Post>(model);
        Validate(post);
        post = await _postRepository.UpdatePost(post);
        return _mapper.Map<PostResponseToGetById>(post);
    }

    public async Task<PostResponseToGetById?> DeletePost(PostRequestToDeleteById request)
    {
        var post = await _postRepository.RemovePost(request.Id);
        return _mapper.Map<PostResponseToGetById>(post);
    }
    
    private bool Validate(Post post)
    {
        var errors = new Dictionary<string, string[]>();
        if (post.Content.Length < 2 || post.Content.Length > 2048)
        {
            errors.Add("Content",["Should be from 2 to 64 chars"]);
        }
        if (errors.Count != 0)
        {
            throw new BadRequestException("Validation error", errors);
        }
        return true;
    }
}