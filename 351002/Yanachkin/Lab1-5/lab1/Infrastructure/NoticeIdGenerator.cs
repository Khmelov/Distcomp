namespace lab1.Infrastructure;

internal static class NoticeIdGenerator
{
    private static long _seq;

    public static long NextId()
    {
        var ms = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
        var n = Interlocked.Increment(ref _seq) & 0xFFFF;
        return (ms << 16) | n;
    }
}
