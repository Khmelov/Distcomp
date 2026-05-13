namespace lab1.Errors;

public sealed class EditorLoginAlreadyExistsException : Exception
{
    public EditorLoginAlreadyExistsException()
        : base("Editor login is already in use")
    {
    }
}
