using Discussion.Models;

namespace Discussion.Repositories.Interfaces;

public interface IMessageRepository : ICassandraRepository<Message>
{
    
}