using Application.DTO.Request;
using Application.DTO.Request.Mark;
using Application.DTO.Response;
using Application.DTO.Response.Mark;
using Application.Interfaces;
using AutoMapper;
using Domain.Entities;
using Domain.Exceptions;
using Domain.Repository;

namespace Application.Services;

public class MarkService (IMarkRepository _markRepository, IMapper _mapper) : IMarkService
{
    public async Task<MarkResponseToGetById?> CreateMark(MarkRequestToCreate model)
    {
        Mark mark = _mapper.Map<Mark>(model);
        Validate(mark);
        mark = await _markRepository.AddMark(mark);
        return _mapper.Map<MarkResponseToGetById>(mark);
    }

    public async Task<IEnumerable<MarkResponseToGetById?>?> GetMarks(MarkRequestToGetAll request)
    {
        var mark = await _markRepository.GetAllMarks();
        return mark.Select(_mapper.Map<MarkResponseToGetById>);
    }

    public async Task<MarkResponseToGetById?> GetMarkById(MarkRequestToGetById request)
    {
        var mark = await _markRepository.GetMark(request.Id);
        return _mapper.Map<MarkResponseToGetById>(mark);
    }

    public async Task<MarkResponseToGetById?> UpdateMark(MarkRequestToFullUpdate model)
    {
        var mark = _mapper.Map<Mark>(model);
        Validate(mark);
        mark = await _markRepository.UpdateMark(mark);
        return _mapper.Map<MarkResponseToGetById>(mark);
    }

    public async Task<MarkResponseToGetById?> DeleteMark(MarkRequestToDeleteById request)
    {
        var mark = await _markRepository.RemoveMark(request.Id);
        return _mapper.Map<MarkResponseToGetById>(mark);
    }
    
    private bool Validate(Mark mark)
    {
        var errors = new Dictionary<string, string[]>();
        if (mark.Name.Length < 2 || mark.Name.Length > 32)
        {
            errors.Add("Name",["Should be from 2 to 32 chars"]);
        }
        if (errors.Count != 0)
        {
            throw new BadRequestException("Validation error", errors);
        }
        return true;
    }
}