using rest1.application.interfaces;
using rest1.core.entities;

namespace rest1.infrastructure.persistence;

public class MarkRepository: Repository<Mark>, IMarkRepository
{
    public Task DeleteMarksWithoutNews()
    {
        throw new NotImplementedException();
    }
}