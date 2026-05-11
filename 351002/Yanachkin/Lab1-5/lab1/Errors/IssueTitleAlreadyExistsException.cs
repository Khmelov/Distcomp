namespace lab1.Errors;

public sealed class IssueTitleAlreadyExistsException : Exception
{
    public IssueTitleAlreadyExistsException()
        : base("Issue title is already in use")
    {
    }
}
