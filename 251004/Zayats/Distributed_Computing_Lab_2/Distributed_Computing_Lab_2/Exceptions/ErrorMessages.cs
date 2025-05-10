namespace Distributed_Computing_Lab_2.Exceptions;

public static class ErrorMessages
{
    public static string UserNotFoundMessage(long id) => $"Author with id {id} was not found.";
    public static string StoryNotFoundMessage(long id) => $"Topic with id {id} was not found.";
    public static string TagNotFoundMessage(long id) => $"Sticker with id {id} was not found.";
    public static string NoticeNotFoundMessage(long id) => $"Message with id {id} was not found.";
}