namespace Additions.Cache;

public class CacheException : Exception
{
    public CacheException() {}
    public CacheException(string? message) : base(message) { }
}