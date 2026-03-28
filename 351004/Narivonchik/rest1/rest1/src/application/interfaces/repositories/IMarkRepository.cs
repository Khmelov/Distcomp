using rest1.core.entities;

namespace rest1.application.interfaces;

public interface IMarkRepository : IRepository<Mark>
{
    Task DeleteMarksWithoutNews();
}