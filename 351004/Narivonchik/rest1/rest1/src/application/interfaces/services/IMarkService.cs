using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;

namespace rest1.application.interfaces.services;

public interface IMarkService
{
    Task<MarkResponseTo> CreateMark(MarkRequestTo createMarkRequestTo);

    Task<IEnumerable<MarkResponseTo>> GetAllMarks();

    Task<MarkResponseTo> GetMark(MarkRequestTo getMarkRequestTo);

    Task<MarkResponseTo> UpdateMark(MarkRequestTo updateMarkRequestTo);

    Task DeleteMark(MarkRequestTo deleteMarkRequestTo);
}