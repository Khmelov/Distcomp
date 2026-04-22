namespace Additions.Messaging;

public class MessagingException : Exception
{
    public MessagingException() {}
    public MessagingException(string? message) : base(message) { }
}

public class MessagingFailedOperationException : MessagingException
{
    public MessagingFailedOperationException() {}
    public MessagingFailedOperationException(string? message) : base(message) { }
}