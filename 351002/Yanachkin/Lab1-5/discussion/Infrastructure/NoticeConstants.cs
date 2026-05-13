namespace discussion.Infrastructure;

/// <summary>
/// Склеивание «плоского» индекса для полного сканирования без горячих партиций:
/// bucket = id % N распределяет строки по N независимым партициям (в отличие от ключа вида country).
/// </summary>
public static class NoticeConstants
{
    public const int ListBucketCount = 256;

    public static int BucketFor(long id)
    {
        var m = id % ListBucketCount;
        if (m < 0)
            m += ListBucketCount;
        return (int)m;
    }
}
