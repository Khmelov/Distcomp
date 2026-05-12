namespace CommonAPI.Service.Events;

public class ManyCommentsPayload
{
    public CommentPayload[] Comments {get;}

    public ManyCommentsPayload(CommentPayload[] comments)
    {
        Comments = comments;
    }
}