public class CommentService : ICommentService
{
    private readonly IRepository<Comment> _repository;

    public CommentService(IRepository<Comment> repository)
    {
        _repository = repository;
    }

    public CommentResponseTo GetById(long id)
    {
        var comment = _repository.GetById(id);
        if (comment == null)
            throw new KeyNotFoundException($"Comment с id={id} не найден");
        return ToResponse(comment);
    }

    public IEnumerable<CommentResponseTo> GetAll() =>
        _repository.GetAll().Select(ToResponse);

    public CommentResponseTo Create(CommentRequestTo request) =>
        ToResponse(_repository.Create(ToModel(request)));

    public CommentResponseTo Update(CommentRequestTo request) =>
        ToResponse(_repository.Update(ToModel(request)));

    public void Delete(long id)
    {
        if (!_repository.Delete(id))
            throw new KeyNotFoundException($"Comment с id={id} не найден");
    }

    private Comment ToModel(CommentRequestTo dto) => new Comment
    {
        Id = dto.Id,
        StoryId = dto.StoryId,
        Content = dto.Content
    };

    private CommentResponseTo ToResponse(Comment model) => new CommentResponseTo
    {
        Id = model.Id,
        StoryId = model.StoryId,
        Content = model.Content
    };
}