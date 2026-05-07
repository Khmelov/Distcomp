using Cassandra;
using RW.Discussion.DTOs;
using ISession = Cassandra.ISession;

namespace RW.Discussion.Services;

public class CassandraNoteService : INoteService
{
    private readonly ISession _session;
    private static long _counter = DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();

    private readonly PreparedStatement _insertStmt;
    private readonly PreparedStatement _selectByIdStmt;
    private readonly PreparedStatement _selectAllStmt;
    private readonly PreparedStatement _updateStmt;
    private readonly PreparedStatement _deleteStmt;

    public CassandraNoteService(ISession session)
    {
        _session = session;

        _insertStmt = _session.Prepare(
            "INSERT INTO tbl_note (id, article_id, content, first_name, last_name) VALUES (?, ?, ?, ?, ?)");

        _selectByIdStmt = _session.Prepare(
            "SELECT id, article_id, content, first_name, last_name FROM tbl_note WHERE id = ?");

        _selectAllStmt = _session.Prepare(
            "SELECT id, article_id, content, first_name, last_name FROM tbl_note");

        _updateStmt = _session.Prepare(
            "UPDATE tbl_note SET article_id = ?, content = ?, first_name = ?, last_name = ? WHERE id = ?");

        _deleteStmt = _session.Prepare(
            "DELETE FROM tbl_note WHERE id = ?");
    }

    private static long NextId() => Interlocked.Increment(ref _counter);

    public async Task<NoteResponseTo> CreateAsync(NoteRequestTo dto)
    {
        var id = NextId();
        var bound = _insertStmt.Bind(id, dto.ArticleId, dto.Content, dto.FirstName, dto.LastName);
        await _session.ExecuteAsync(bound);

        return new NoteResponseTo
        {
            Id = id,
            ArticleId = dto.ArticleId,
            Content = dto.Content,
            FirstName = dto.FirstName,
            LastName = dto.LastName
        };
    }

    public async Task<NoteResponseTo?> GetByIdAsync(long id)
    {
        var bound = _selectByIdStmt.Bind(id);
        var result = await _session.ExecuteAsync(bound);
        var row = result.FirstOrDefault();
        if (row == null)
            return null;

        return MapRow(row);
    }

    public async Task<IEnumerable<NoteResponseTo>> GetAllAsync()
    {
        var bound = _selectAllStmt.Bind();
        var result = await _session.ExecuteAsync(bound);
        return result.Select(MapRow).ToList();
    }

    public async Task<NoteResponseTo?> UpdateAsync(NoteRequestTo dto)
    {
        var existing = await GetByIdAsync(dto.Id);
        if (existing == null)
            return null;

        var bound = _updateStmt.Bind(dto.ArticleId, dto.Content, dto.FirstName, dto.LastName, dto.Id);
        await _session.ExecuteAsync(bound);

        return new NoteResponseTo
        {
            Id = dto.Id,
            ArticleId = dto.ArticleId,
            Content = dto.Content,
            FirstName = dto.FirstName,
            LastName = dto.LastName
        };
    }

    public async Task<bool> DeleteAsync(long id)
    {
        var existing = await GetByIdAsync(id);
        if (existing == null)
            return false;

        var bound = _deleteStmt.Bind(id);
        await _session.ExecuteAsync(bound);
        return true;
    }

    private static NoteResponseTo MapRow(Row row)
    {
        return new NoteResponseTo
        {
            Id = row.GetValue<long>("id"),
            ArticleId = row.GetValue<long>("article_id"),
            Content = row.GetValue<string>("content"),
            FirstName = row.GetValue<string>("first_name"),
            LastName = row.GetValue<string>("last_name")
        };
    }
}
