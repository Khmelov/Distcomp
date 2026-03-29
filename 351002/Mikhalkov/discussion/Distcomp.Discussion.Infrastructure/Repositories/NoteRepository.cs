using Cassandra;
using Distcomp.Discussion.Domain.Models;
using Distcomp.Discussion.Infrastructure.Data;

namespace Distcomp.Discussion.Infrastructure.Repositories
{
    public class NoteRepository
    {
        private readonly ISession _session;
        private readonly PreparedStatement _insertStmt;

        public NoteRepository(CassandraProvider provider)
        {
            _session = provider.Session;
            _insertStmt = _session.Prepare("INSERT INTO tbl_note (country, issue_id, id, content) VALUES (?, ?, ?, ?)");
        }

        public void Update(Note note) => Save(note);

        public void Save(Note note) =>
            _session.Execute(_insertStmt.Bind(note.Country, note.IssueId, note.Id, note.Content));

        public Note? GetOne(string country, long issueId, long id)
        {
            var row = _session.Execute($"SELECT * FROM tbl_note WHERE country='{country}' AND issue_id={issueId} AND id={id}").FirstOrDefault();
            return row == null ? null : Map(row);
        }

        public bool Exists(string country, long issueId, long id)
        {
            var row = _session.Execute($"SELECT id FROM tbl_note WHERE country='{country}' AND issue_id={issueId} AND id={id}").FirstOrDefault();
            return row != null;
        }

        public IEnumerable<Note> GetAll() =>
            _session.Execute("SELECT * FROM tbl_note").Select(Map);

        public void Delete(string country, long issueId, long id) =>
            _session.Execute($"DELETE FROM tbl_note WHERE country='{country}' AND issue_id={issueId} AND id={id}");

        private Note Map(Row row) => new Note
        {
            Country = row.GetValue<string>("country"),
            IssueId = row.GetValue<long>("issue_id"),
            Id = row.GetValue<long>("id"),
            Content = row.GetValue<string>("content")
        };
    }
}