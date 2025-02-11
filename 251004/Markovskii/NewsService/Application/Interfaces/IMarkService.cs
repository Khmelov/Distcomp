using Application.DTO.Request;
using Application.DTO.Request.Mark;
using Application.DTO.Response;
using Application.DTO.Response.Mark;

namespace Application.Interfaces;

public interface IMarkService
{
    public Task<MarkResponseToGetById?> CreateMark(MarkRequestToCreate model);
    public Task<IEnumerable<MarkResponseToGetById?>?> GetMarks(MarkRequestToGetAll request);
    public Task<MarkResponseToGetById?> GetMarkById(MarkRequestToGetById request);
    public Task<MarkResponseToGetById?> UpdateMark(MarkRequestToFullUpdate model);
    public Task<MarkResponseToGetById?> DeleteMark(MarkRequestToDeleteById request);
}