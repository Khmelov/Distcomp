using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DTO.responces
{
    public class NoteResponseTo
    {
        public long Id { get; set; }
        public long StoryId { get; set; }
        public string Content { get; set; } = string.Empty;
    }
}
