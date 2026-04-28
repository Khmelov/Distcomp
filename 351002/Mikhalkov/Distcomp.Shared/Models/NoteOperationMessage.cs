namespace Distcomp.Shared.Models
{
    public enum NoteOperation
    {
        GET_ALL,
        GET_BY_ID,
        UPDATE,
        DELETE,
        CREATE
    }

    public class NoteOperationMessage
    {
        public string CorrelationId { get; set; } = Guid.NewGuid().ToString();
        public NoteOperation Operation { get; set; }
        public long? NoteId { get; set; }
        public Note? Note { get; set; }
        public string? Country { get; set; }
        public long? IssueId { get; set; }
    }
}