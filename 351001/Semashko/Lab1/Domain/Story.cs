using System.ComponentModel.DataAnnotations;

namespace lab_1.Domain
{
    public class Story:BaseEntity
    {
        private long? _authorId;
     
        private string _title;
        
        private string _content;
        private DateTime _created;
        private DateTime _modified;

        public Story (long? id ,long? authorId, string title, string content, DateTime created, DateTime modified) : base (id)
        {
            _authorId = authorId;
            _title = title;
            _content = content;
            _created = created;
            _modified = modified;
        }

        public long? AuthorId { get => _authorId; }
        [StringLength(64,MinimumLength = 2)]
        public string Title { get => _title; }
        [StringLength(2048,MinimumLength = 4)]
        public string Content { get => _content; }
        public DateTime Created { get => _created; }
        public DateTime Modified { get => _modified;
        }
    }
}
