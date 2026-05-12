using lab_1.Domain;

namespace lab_1.Dtos.ResponseDtos.ResponseConverters
{
    public class ListCommentResponseConverter
    {
        private CommentResponseConverter commentResponseConverter = new CommentResponseConverter();
        public IEnumerable<CommentResponseDto> CommentsResponse(IEnumerable<Comment> list)
        {
            foreach (Comment node in list)
                yield return commentResponseConverter.ToDto(node);
        }
    }
}
