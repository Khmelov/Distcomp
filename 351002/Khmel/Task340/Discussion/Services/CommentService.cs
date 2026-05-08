using Discussion.DTOs;
using Discussion.Models;
using Discussion.Repositories;

namespace Discussion.Services
{
    public interface ICommentService
    {
        Task<IEnumerable<CommentResponseTo>> GetAllAsync();
        Task<CommentResponseTo> GetByIdAsync(long id);
        Task<CommentResponseTo> CreateAsync(CommentRequestTo request);
        Task<CommentResponseTo> UpdateAsync(long id, CommentRequestTo request);
        Task DeleteAsync(long id);
    }

    public class CommentService : ICommentService
    {
        private readonly ICommentRepository _repository;

        public CommentService(ICommentRepository repository)
        {
            _repository = repository;
        }

        public async Task<IEnumerable<CommentResponseTo>> GetAllAsync()
        {
            var comments = await _repository.GetAllAsync();
            return comments.Select(ToResponse);
        }

        public async Task<CommentResponseTo> GetByIdAsync(long id)
        {
            var comment = await _repository.GetByIdAsync(id);
            if (comment == null)
                return null!; 
            return ToResponse(comment);
        }

        public async Task<CommentResponseTo> CreateAsync(CommentRequestTo request)
        {
            ValidateContent(request.Content);
            
            var comment = new Comment
            {
                Id = request.Id,
                StoryId = request.StoryId,
                Content = request.Content,
                State = "PENDING",
                Country = "",
                Created = DateTimeOffset.UtcNow
            };

            var created = await _repository.CreateAsync(comment);
            return ToResponse(created);
        }

        public async Task<CommentResponseTo> UpdateAsync(long id, CommentRequestTo request)
        {
            ValidateContent(request.Content);
            
            var existing = await _repository.GetByIdAsync(id);
            if (existing == null)
            {
                var comment = new Comment
                {
                    Id = id,
                    StoryId = request.StoryId,
                    Content = request.Content,
                    State = "PENDING",
                    Country = "",
                    Created = DateTimeOffset.UtcNow
                };
                await _repository.CreateAsync(comment);
                return ToResponse(comment);
            }

            existing.Content = request.Content;
            if (request.StoryId > 0)
                existing.StoryId = request.StoryId;

            var updated = await _repository.UpdateAsync(existing);
            return ToResponse(updated);
        }

        public async Task DeleteAsync(long id)
        {
            await _repository.DeleteAsync(id);
        }

        private void ValidateContent(string content)
        {
            if (content.Length < 2 || content.Length > 2048)
                throw new ArgumentException("Content: 2-2048 chars");
        }

        private CommentResponseTo ToResponse(Comment model) => new CommentResponseTo
        {
            Id = model.Id,
            StoryId = model.StoryId,
            Content = model.Content
        };
    }
}