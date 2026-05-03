using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Publisher.Dtos
{
    public class ReactionResponseTo
    {
        public long Id { get; set; }
        public long TopicId { get; set; }
        public string Content { get; set; } = null!;
        public string? State { get; set; }
    }
}
