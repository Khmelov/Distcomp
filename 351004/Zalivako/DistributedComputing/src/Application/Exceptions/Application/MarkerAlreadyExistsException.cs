using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Application.Exceptions.Application
{
    public class MarkerAlreadyExistsException : AlreadyExistsException
    {
        public MarkerAlreadyExistsException(string message) : base(message) { }

        public MarkerAlreadyExistsException(string message, Exception inner) : base(message, inner) { }
    }
}
