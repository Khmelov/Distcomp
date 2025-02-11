using Domain.Entities;
using Domain.Exceptions;
using Domain.Repository;

namespace Infrastructure.Repositories.InMemoryRepositories;

public class InMemoryMarkRepository : IMarkRepository
{
    private readonly Dictionary<long, Mark> _marks = new();
    private long _nextId = 1;
    
    public async Task<Mark?> AddMark(Mark mark)
    {
        if (_marks.Values.All(obj => obj.Name!= mark.Name))
        {
            mark.Id = _nextId;
            _marks.Add(_nextId,mark);
            return _marks[_nextId++];
        }
        else
        {
            throw new BadRequestException("Such name already exists", new Dictionary<string, string[]>());
        }
    }

    public async Task<Mark?> GetMark(long markId)
    {
        if (_marks.TryGetValue(markId, out Mark mark))
        {
            return mark;
        }
        throw new NotFoundException("Id",$"{markId}");
    }

    public async Task<Mark?> RemoveMark(long markId)
    {
        if (_marks.Remove(markId, out Mark mark))
        {
            return mark;
        }
        throw new NotFoundException("Id",$"{markId}");
    }

    public async Task<Mark?> UpdateMark(Mark mark)
    {
        if (_marks.ContainsKey(mark.Id))
        {
            if (_marks.Values.All(obj => obj.Name != mark.Name))
            {
                _marks[mark.Id] = mark;
                return _marks[mark.Id];
            }
            else
            {
                throw new BadRequestException("Such name already exists", new Dictionary<string, string[]>());
            }
        }
        throw new NotFoundException("Id",$"{mark.Id}");
    }

    public async Task<IEnumerable<Mark?>?> GetAllMarks()
    {
        return  _marks.Values.ToList();
    }
}