using Distributed_Computing_Lab_1.Models;
using Distributed_Computing_Lab_1.Repositories.Interfaces;

namespace Distributed_Computing_Lab_1.Repositories.Implementations;

public class InMemoryMessageRepository : BaseInMemoryRepository<Message>, IMesssageRepository
{
    
}