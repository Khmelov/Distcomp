namespace Distributed_Computing_Lab_1.Exceptions;

public static class ErrorMessages
{
    public static string UserNotFoundMessage(long id) => $"Author with id {id} was not found.";
    public static string StoryNotFoundMessage(long id) => $"Topic with id {id} was not found.";
    public static string TagNotFoundMessage(long id) => $"Sticker with id {id} was not found.";
    public static string NoticeNotFoundMessage(long id) => $"Message with id {id} was not found.";

    public static string UserAlreadyExists(string login) => $"Author with login '{login}' already exists.";
    public static string StoryAlreadyExists(string title) => $"Topic with title '{title}' already exists.";
    public static string TagAlreadyExists(string tag) => $"Sticker with name '{tag}' already exists.";
}