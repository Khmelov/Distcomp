using lab_1.Context;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.RequestDtos.RequestConverters;
using lab_1.Dtos.ResponseDtos;
using lab_1.Dtos.ResponseDtos.ResponseConverters;
using lab_1.Entities;
using Microsoft.EntityFrameworkCore;

namespace lab_1.Services;

public class CommentService:IBaseService<CommentRequestDto,CommentResponseDto>
{
    private AppbContext _context;
    private BaseRequest<TblComment, CommentRequestDto> _request;
    private BaseResponse<CommentResponseDto, TblComment> _response;

    public CommentService()
    {
    }
    
    public CommentService(AppbContext context)
    {
        _context = context;
        _request = new ();
        _response = new ();
    }

    public CommentResponseDto Create(CommentRequestDto dto)
    {
        dto.Id = _context.TblComments.Count() + 1;
        using (_context)
        {
            var entity = _request.FromDto(dto);
            _context.TblComments.Add(entity);
            _context.SaveChanges();
            return _response.ToDto(entity);
        }
    }

    public CommentResponseDto? Read(long id)
    {
        using (_context)
        {
            var entity = _context.TblComments.Find(id);
            return _response.ToDto(entity);
        }
    }

    public CommentResponseDto? Update(CommentRequestDto dto)
    {
        using (_context)
        {
            var entity = _request.FromDto(dto);
            _context.TblComments.Update(entity);
            _context.SaveChanges();
            return _response.ToDto(entity);
        }
    }

    public bool Delete(long id)
    {
        using (_context)
        {
            var res =_context.TblComments.Remove(_context.TblComments.Find(id)).State==EntityState.Deleted;
            _context.SaveChanges();
            return res;
        }
    }

    public IEnumerable<CommentResponseDto> GetAll()
    {
        using (_context)
        {
            foreach (var entity in _context.TblComments)
            {
                yield return _response.ToDto(entity);
            }
        }
    }
}