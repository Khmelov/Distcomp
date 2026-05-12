namespace discussion.Infrastructure;

/// <summary>Упрощённая модерация по стоп-словам (задание Task340).</summary>
public static class NoticeModerator
{
    private static readonly string[] StopWords =
    [
        "spam", "scam", "реклама", "порно", "казино"
    ];

    public static string Evaluate(string trimmedContent)
    {
        foreach (var w in StopWords)
        {
            if (trimmedContent.Contains(w, StringComparison.OrdinalIgnoreCase))
                return "DECLINE";
        }

        return "APPROVE";
    }
}
