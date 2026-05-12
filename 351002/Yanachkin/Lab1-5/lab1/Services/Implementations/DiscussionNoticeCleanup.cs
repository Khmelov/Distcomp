namespace lab1.Services.Implementations;

/// <summary>Удаление обсуждений в Cassandra при удалении issue.</summary>
public interface IDiscussionNoticeCleanup
{
    Task DeleteAllForIssueAsync(long issueId, CancellationToken cancellationToken = default);
}

public class DiscussionNoticeCleanup : IDiscussionNoticeCleanup
{
    private readonly HttpClient _http;

    public DiscussionNoticeCleanup(IHttpClientFactory httpClientFactory)
    {
        _http = httpClientFactory.CreateClient("Discussion");
    }

    public async Task DeleteAllForIssueAsync(long issueId, CancellationToken cancellationToken = default)
    {
        using var res = await _http.DeleteAsync($"api/v1.0/Notices/for-issue/{issueId}", cancellationToken)
            .ConfigureAwait(false);
        if (res.IsSuccessStatusCode)
            return;

        if ((int)res.StatusCode >= 500)
            res.EnsureSuccessStatusCode();

        // 404 discussion или прочие ошибки не блокируем удаление issue в демо-сценарии
        _ = await res.Content.ReadAsStringAsync(cancellationToken).ConfigureAwait(false);
    }
}
