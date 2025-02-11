using Domain.Entities;

namespace Domain.Repository;

public interface IMarkRepository
{
    Task<Mark?> AddMark(Mark mark);
    Task<Mark?> GetMark(long markId);
    Task<Mark?> RemoveMark(long markId);
    Task<Mark?> UpdateMark(Mark mark);
    Task<IEnumerable<Mark?>?> GetAllMarks();
}