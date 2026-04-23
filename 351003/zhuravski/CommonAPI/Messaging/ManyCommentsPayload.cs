namespace CommonAPI.Messaging;

public class ManyCommentsPayload
{
    public CommentPayload[] Comments {get;}

    public ManyCommentsPayload(CommentPayload[] comments)
    {
        Comments = comments;
    }
}