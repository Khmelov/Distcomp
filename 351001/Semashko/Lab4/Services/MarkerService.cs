using lab_1.Context;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.RequestDtos.RequestConverters;
using lab_1.Dtos.ResponseDtos;
using lab_1.Dtos.ResponseDtos.ResponseConverters;
using lab_1.Entities;
using Microsoft.EntityFrameworkCore;

namespace lab_1.Services;

public class MarkerService:IBaseService<MarkerRequestDto,MarkerResponseDto>
{
    private AppbContext _context;
    private BaseRequest<TblMarker, MarkerRequestDto> _request;
    private BaseResponse<MarkerResponseDto, TblMarker> _response;
    public MarkerService()
    {
    }
    
    public MarkerService(AppbContext context)
    {
        _context = context;
        _request = new ();
        _response = new ();
    }
    public MarkerResponseDto Create(MarkerRequestDto dto)
    {
        dto.Id = _context.TblMarkers.Count() + 1;
        using (_context)
        {
            var entity = _request.FromDto(dto);
            _context.TblMarkers.Add(entity);
            _context.SaveChanges();
            return _response.ToDto(entity);
        }
    }

    public MarkerResponseDto? Read(long id)
    {
        using (_context)
        {
            var entity = _context.TblMarkers.Find(id);
            return _response.ToDto(entity);
        }
    }

    public MarkerResponseDto? Update(MarkerRequestDto dto)
    {
        using (_context)
        {
            var entity = _request.FromDto(dto);
            _context.TblMarkers.Update(entity);
            _context.SaveChanges();
            return _response.ToDto(entity);
        }
    }

    public bool Delete(long id)
    {
        using (_context)
        {
            var entity = _context.TblMarkers.Find(id);
            if (entity != null)
            {
                var res = _context.TblMarkers.Remove(entity).State == EntityState.Deleted;
                _context.SaveChanges();
                return res;
            }
            return false;
        }
    }

    public IEnumerable<MarkerResponseDto> GetAll()
    {
        using (_context)
        {
            foreach (var entity in _context.TblMarkers)
            {
                yield return _response.ToDto(entity);
            }
        }
    }

    public Task<MarkerResponseDto> CreateAsync(MarkerRequestDto dto)
    {
        throw new NotImplementedException();
    }
}
