using System.ComponentModel;
using System.Diagnostics.Eventing.Reader;

namespace lab_1.Domain
{
    public class BaseEntity
    {
        
        protected long? _id;
        
        [DefaultValue(-1)]
        public long? Id { get => _id; }

        public BaseEntity(long? id) 
        {
            _id = id;
        }
    }
}
