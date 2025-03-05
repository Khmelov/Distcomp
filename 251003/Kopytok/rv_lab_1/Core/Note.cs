using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Core
{
    public class Note
    {
        public long Id { get; set; } = 0;
        public long StoryId { get; set; } = 0;
        public String Content { get; set; } = string.Empty;
    }
}
