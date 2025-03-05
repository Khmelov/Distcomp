using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DTO.requests
{
    public class EditorRequestTo
    {
        public string login { get; set; } = string.Empty;
        public string password { get; set; } = string.Empty;
        public string firstname { get; set; } = string.Empty;
        public string lastname { get; set; } = string.Empty;
    }
}
