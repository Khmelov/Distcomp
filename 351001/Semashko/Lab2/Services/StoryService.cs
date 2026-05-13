using lab_1.Context;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.RequestDtos.RequestConverters;
using lab_1.Dtos.ResponseDtos;
using lab_1.Dtos.ResponseDtos.ResponseConverters;
using lab_1.Entities;
using Microsoft.EntityFrameworkCore;

namespace lab_1.Services;

public class StoryService:IBaseService<StoryRequestDto,StoryResponseDto>
{
    
    private AppbContext _context;
    private BaseRequest<TblStory, StoryRequestDto> _request;
    private BaseResponse<StoryResponseDto, TblStory> _response;

    public StoryService()
    {
    }

    public StoryService(AppbContext context)
    {
        _context = context;
        _request = new ();
        _response = new ();
    }

    public StoryResponseDto Create(StoryRequestDto dto)
    {
        dto.Id = _context.TblStories.Count() + 1;
        dto.Created = DateOnly.FromDateTime(DateTime.Now);
        dto.Modified = DateOnly.FromDateTime(DateTime.Now);
        using (_context)
        {
            var entity = _request.FromDto(dto);
            _context.TblStories.Add(entity);
            _context.SaveChanges();
            return _response.ToDto(entity);
        }
    }

    public StoryResponseDto? Read(long id)
    {
        using (_context)
        {
            var entity = _context.TblStories.Find(id);
            return _response.ToDto(entity);
        }
    }

    public StoryResponseDto? Update(StoryRequestDto dto)
    {
         
        using (_context)
        {
            dto.Created = DateOnly.FromDateTime(DateTime.Now);
            dto.Modified = DateOnly.FromDateTime(DateTime.Now);
            var entity = _request.FromDto(dto);
            _context.TblStories.Update(entity);
            _context.SaveChanges();
            return _response.ToDto(entity);
        }
    }

    public bool Delete(long id)
    {
        using (_context)
        {
            var res =_context.TblStories.Remove(_context.TblStories.Find(id)).State==EntityState.Deleted;
            _context.SaveChanges();
            return res;
        }
    }

    public IEnumerable<StoryResponseDto> GetAll()
    {
        using (_context)
        {
            foreach (var entity in _context.TblStories)
            {
                yield return _response.ToDto(entity);
            }
        }
    }
}