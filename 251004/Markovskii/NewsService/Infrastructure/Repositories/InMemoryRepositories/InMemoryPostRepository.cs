using Domain.Entities;
using Domain.Exceptions;
using Domain.Repository;

namespace Infrastructure.Repositories.InMemoryRepositories;

public class InMemoryPostRepository : IPostRepository
{
    private readonly Dictionary<long, Post> _posts = new ();
    private long _nextId = 1;
    
    public async Task<Post?> AddPost(Post post)
    {
        post.Id = _nextId;
        _posts.Add(_nextId,post);
        return _posts[_nextId++];
    }

    public async Task<Post?> GetPost(long postId)
    {
        if (_posts.TryGetValue(postId, out Post post))
        {
            return post;
        }
        throw new NotFoundException("Id",$"{postId}");
    }

    public async Task<Post?> RemovePost(long postId)
    {
        if (_posts.Remove(postId, out Post post))
        {
            return post;
        }
        throw new NotFoundException("Id",$"{postId}");
    }

    public async Task<Post?> UpdatePost(Post post)
    {
        if (_posts.ContainsKey(post.Id))
        {
            _posts[post.Id] = post;
            return _posts[post.Id];
        }
        throw new NotFoundException("Id",$"{post.Id}");
    }

    public async Task<IEnumerable<Post?>?> GetAllPosts()
    {
        return _posts.Values.ToList();
    }

    public async Task<IEnumerable<Post?>?> GetPostsByNewsId(long newsId)
    {
        return _posts.Values.Where(obj => obj.NewsId == newsId);
    }
}