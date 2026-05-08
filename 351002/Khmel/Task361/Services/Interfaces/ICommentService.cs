public interface ICommentService
{
    CommentResponseTo GetById(long id);
    IEnumerable<CommentResponseTo> GetAll();
    CommentResponseTo Create(CommentRequestTo request);
    CommentResponseTo Update(CommentRequestTo request);
    void Delete(long id);
}