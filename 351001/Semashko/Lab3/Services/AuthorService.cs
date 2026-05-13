using lab_1.Context;
using lab_1.Dtos.RequestDtos;
using lab_1.Dtos.RequestDtos.RequestConverters;
using lab_1.Dtos.ResponseDtos;
using lab_1.Dtos.ResponseDtos.ResponseConverters;
using lab_1.Entities;
using lab_1.Services.Validtors;
using Microsoft.EntityFrameworkCore;

namespace lab_1.Services;

public class AuthorService : IBaseService<AuthorRequestDto, AuthorResponseDto>
{
    private AppbContext _context;
    private BaseRequest<TblAuthor, AuthorRequestDto> _request;
    private BaseResponse<AuthorResponseDto, TblAuthor> _response;

    public AuthorService()
    {
    }

    public AuthorService(AppbContext context)
    {
        _context = context;
        _request = new();
        _response = new();
    }

    public AuthorResponseDto? Create(AuthorRequestDto dto)
    {
        dto.Id = _context.TblAuthors.Count() + 1;
        using (_context)
        {
            var entity = _request.FromDto(dto);
            _context.TblAuthors.Add(entity);
            _context.SaveChanges();
            return _response.ToDto(entity);
        }
    }

    public AuthorResponseDto? Read(long id)
    {
        using (_context)
        {
            var entity = _context.TblAuthors.Find(id);
            return _response.ToDto(entity);
        }
    }

    public AuthorResponseDto? Update(AuthorRequestDto dto)
    {
        using (_context)
        {
            var entity = _request.FromDto(dto);
            _context.TblAuthors.Update(entity);
            _context.SaveChanges();
            return _response.ToDto(entity);
        }
    }

    public bool Delete(long id)
    {
        using (_context)
        {
            var res = _context.TblAuthors.Remove(_context.TblAuthors.Find(id)).State == EntityState.Deleted;
            _context.SaveChanges();
            return res;
        }
    }

    public IEnumerable<AuthorResponseDto> GetAll()
    {
        using (_context)
        {
            foreach (var entity in _context.TblAuthors)
            {
                yield return _response.ToDto(entity);
            }
        }
    }
}