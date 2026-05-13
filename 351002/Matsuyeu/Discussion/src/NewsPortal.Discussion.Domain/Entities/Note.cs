namespace Discussion.src.NewsPortal.Discussion.Domain.Entities
{
    public enum NoteState
    {
        PENDING,    //Ожидает модерации
        APPROVED,   //Одобрено
        DECLINED    //Отклонено
    }

    public class Note
    {
        public long Id { get; set; }
        public long NewsId { get; set; }
        public string Content { get; set; } = string.Empty;
        public string State { get; set; } = "PENDING";
    }
}
