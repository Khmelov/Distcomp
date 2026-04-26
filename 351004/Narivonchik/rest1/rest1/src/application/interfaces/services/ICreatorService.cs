using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;

namespace rest1.application.interfaces.services;

public interface ICreatorService
{
    Task<CreatorResponseTo> CreateCreator(CreatorRequestTo createCreatorRequestTo);

    Task<IEnumerable<CreatorResponseTo>> GetAllCreators();

    Task<CreatorResponseTo> GetCreator(CreatorRequestTo getCreatorRequestTo);

    Task<CreatorResponseTo> UpdateCreator(CreatorRequestTo updateCreatorRequestTo);

    Task DeleteCreator(CreatorRequestTo deleteCreatorRequestTo);
}