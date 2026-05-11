using Cassandra;
using discussion.Infrastructure;
using discussion.Models.Domain;

namespace discussion.Repositories;

public class CassandraNoticeRepository : INoticeRepository
{
    private readonly Cassandra.ISession _session;

    public CassandraNoticeRepository(Cassandra.ISession session)
    {
        _session = session;
    }

    public async Task<NoticeEntity?> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        cancellationToken.ThrowIfCancellationRequested();
        var rs = await _session.ExecuteAsync(new SimpleStatement(
            "SELECT issue_id, content, state FROM tbl_notice_by_id WHERE id = ?", id)).ConfigureAwait(false);
        var row = rs.FirstOrDefault();
        if (row == null)
            return null;
        return new NoticeEntity(
            id,
            row.GetValue<long>("issue_id"),
            row.GetValue<string>("content"),
            row.IsNull("state") ? "APPROVE" : row.GetValue<string>("state"));
    }

    public async Task<IReadOnlyList<NoticeEntity>> GetByIssueIdAsync(long issueId, CancellationToken cancellationToken = default)
    {
        cancellationToken.ThrowIfCancellationRequested();
        var rs = await _session.ExecuteAsync(new SimpleStatement(
            "SELECT id, content, state FROM tbl_notice_by_issue WHERE issue_id = ?", issueId)).ConfigureAwait(false);
        return rs.Select(r =>
        {
            var st = r.IsNull("state") ? "APPROVE" : r.GetValue<string>("state");
            return new NoticeEntity(r.GetValue<long>("id"), issueId, r.GetValue<string>("content"), st);
        }).ToList();
    }

    public async Task<IReadOnlyList<NoticeEntity>> GetAllFromBucketsAsync(CancellationToken cancellationToken = default)
    {
        cancellationToken.ThrowIfCancellationRequested();
        var n = NoticeConstants.ListBucketCount;
        var tasks = new Task<IReadOnlyList<NoticeEntity>>[n];
        for (var b = 0; b < n; b++)
            tasks[b] = ReadBucketAsync(b, cancellationToken);

        var parts = await Task.WhenAll(tasks).ConfigureAwait(false);
        var list = new List<NoticeEntity>(parts.Sum(p => p.Count));
        foreach (var part in parts)
            list.AddRange(part);

        return list;
    }

    private async Task<IReadOnlyList<NoticeEntity>> ReadBucketAsync(int bucket, CancellationToken cancellationToken)
    {
        cancellationToken.ThrowIfCancellationRequested();
        var rs = await _session.ExecuteAsync(new SimpleStatement(
            "SELECT id, issue_id, content, state FROM tbl_notice_bucket WHERE bucket = ?", bucket)).ConfigureAwait(false);

        var rows = rs.Select(r =>
        {
            var st = r.IsNull("state") ? "APPROVE" : r.GetValue<string>("state");
            return new NoticeEntity(
                r.GetValue<long>("id"),
                r.GetValue<long>("issue_id"),
                r.GetValue<string>("content"),
                st);
        }).ToList();

        return rows;
    }

    public Task InsertAsync(NoticeEntity entity, CancellationToken cancellationToken = default)
        => UpsertAsync(entity, cancellationToken);

    public async Task UpdateAsync(NoticeEntity previous, NoticeEntity updated, CancellationToken cancellationToken = default)
    {
        cancellationToken.ThrowIfCancellationRequested();
        if (previous.IssueId != updated.IssueId)
        {
            await _session.ExecuteAsync(new SimpleStatement(
                "DELETE FROM tbl_notice_by_issue WHERE issue_id = ? AND id = ?",
                previous.IssueId, updated.Id)).ConfigureAwait(false);
        }

        if (!string.Equals(previous.Content, updated.Content, StringComparison.Ordinal)
            || previous.IssueId != updated.IssueId
            || !string.Equals(previous.State, updated.State, StringComparison.Ordinal))
        {
            await _session.ExecuteAsync(new SimpleStatement(
                "INSERT INTO tbl_notice_bucket (bucket, id, issue_id, content, state) VALUES (?, ?, ?, ?, ?)",
                NoticeConstants.BucketFor(updated.Id), updated.Id, updated.IssueId, updated.Content, updated.State))
                .ConfigureAwait(false);
        }

        await UpsertCoreAsync(updated, cancellationToken);
    }

    public async Task DeleteAsync(long id, CancellationToken cancellationToken = default)
    {
        cancellationToken.ThrowIfCancellationRequested();
        var existing = await GetByIdAsync(id, cancellationToken).ConfigureAwait(false);
        if (existing == null)
            return;

        await _session.ExecuteAsync(new SimpleStatement(
            "DELETE FROM tbl_notice_by_issue WHERE issue_id = ? AND id = ?",
            existing.IssueId, id)).ConfigureAwait(false);
        await _session.ExecuteAsync(new SimpleStatement("DELETE FROM tbl_notice_by_id WHERE id = ?", id)).ConfigureAwait(false);
        await _session.ExecuteAsync(new SimpleStatement(
            "DELETE FROM tbl_notice_bucket WHERE bucket = ? AND id = ?",
            NoticeConstants.BucketFor(id), id)).ConfigureAwait(false);
    }

    public async Task DeleteAllByIssueIdAsync(long issueId, CancellationToken cancellationToken = default)
    {
        cancellationToken.ThrowIfCancellationRequested();
        var rows = await GetByIssueIdAsync(issueId, cancellationToken).ConfigureAwait(false);
        foreach (var e in rows)
            await DeleteAsync(e.Id, cancellationToken).ConfigureAwait(false);
    }

    private async Task UpsertAsync(NoticeEntity entity, CancellationToken cancellationToken)
    {
        cancellationToken.ThrowIfCancellationRequested();
        await _session.ExecuteAsync(new SimpleStatement(
                "INSERT INTO tbl_notice_bucket (bucket, id, issue_id, content, state) VALUES (?, ?, ?, ?, ?)",
                NoticeConstants.BucketFor(entity.Id), entity.Id, entity.IssueId, entity.Content, entity.State))
            .ConfigureAwait(false);
        await UpsertCoreAsync(entity, cancellationToken);
    }

    private Task UpsertCoreAsync(NoticeEntity entity, CancellationToken cancellationToken)
    {
        cancellationToken.ThrowIfCancellationRequested();
        return Task.WhenAll(
            _session.ExecuteAsync(new SimpleStatement(
                "INSERT INTO tbl_notice_by_issue (issue_id, id, content, state) VALUES (?, ?, ?, ?)",
                entity.IssueId, entity.Id, entity.Content, entity.State)),
            _session.ExecuteAsync(new SimpleStatement(
                "INSERT INTO tbl_notice_by_id (id, issue_id, content, state) VALUES (?, ?, ?, ?)",
                entity.Id, entity.IssueId, entity.Content, entity.State)));
    }
}
