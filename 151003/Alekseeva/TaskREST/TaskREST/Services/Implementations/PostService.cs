using Microsoft.EntityFrameworkCore;
using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;
using TaskREST.Exceptions;
using TaskREST.Services.Interfaces;
using TaskREST.Storage;
using PostMapper = TaskREST.Mappers.PostMapper;

namespace TaskREST.Services.Implementations;

public sealed class PostService(AppDbContext context) : IPostService
{
    public async Task<PostResponseTo> GetPostById(long id)
    {
        var post = await context.Posts.FindAsync(id);
        if (post == null) throw new EntityNotFoundException($"Post with id = {id} doesn't exist.");

        return PostMapper.Map(post);
    }

    public async Task<IEnumerable<PostResponseTo>> GetPosts()
    {
        return PostMapper.Map(await context.Posts.ToListAsync());
    }

    public async Task<PostResponseTo> CreatePost(CreatePostRequestTo createPostRequestTo)
    {
        var post = PostMapper.Map(createPostRequestTo);
        await context.Posts.AddAsync(post);
        await context.SaveChangesAsync();
        return PostMapper.Map(post);
    }

    public async Task DeletePost(long id)
    {
        var post = await context.Posts.FindAsync(id);
        if (post == null) throw new EntityNotFoundException($"Post with id = {id} doesn't exist.");

        context.Posts.Remove(post);
        await context.SaveChangesAsync();
    }

    public async Task<PostResponseTo> UpdatePost(UpdatePostRequestTo modifiedPost)
    {
        var post = await context.Posts.FindAsync(modifiedPost.Id);
        if (post == null) throw new EntityNotFoundException($"Post with id = {modifiedPost.Id} doesn't exist.");

        context.Entry(post).State = EntityState.Modified;

        post.Id = modifiedPost.Id;
        post.Content = modifiedPost.Content;
        post.TweetId = modifiedPost.TweetId;

        await context.SaveChangesAsync();
        return PostMapper.Map(post);
    }
}