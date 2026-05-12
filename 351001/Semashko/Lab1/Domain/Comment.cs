using System.ComponentModel.DataAnnotations;

namespace lab_1.Domain
{
    public class Comment:BaseEntity
    {
        private long? _storyId;
       
        private string _content;

        public Comment(long? id,long? storyId, string content) :base(id) 
        {
            _storyId = storyId;
            _content = content;
        }

        public long? StoryId { get => _storyId;  }
        [StringLength(2048,MinimumLength = 2)]
        public string Content {  get => _content; }
    }
}
