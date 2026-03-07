using Additions.Service;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Service.Implementations;

public class CreatorService : Service, ICreatorService
{
    private readonly ICreatorDAO dao;

    public CreatorService(ICreatorDAO dao)
    {
        this.dao = dao;
    }
    public async Task<CreatorResponseDTO[]> GetAllCreatorsAsync()
    {
        CreatorModel[] daoModels = await InvokeDAOMethod(() => dao.GetAllAsync());
        return [.. daoModels.Select(MakeResponseFromModel)];
    }

    public async Task<CreatorResponseDTO> CreateCreatorAsync(CreatorRequestDTO dto)
    {
        CreatorModel model = MakeModelFromRequest(dto);
        CreatorModel result = await InvokeDAOMethod(() => dao.AddNewAsync(model));
        return MakeResponseFromModel(result);
    }

    public async Task DeleteCreatorAsync(long id)
    {
        await InvokeDAOMethod(() => dao.DeleteAsync(id));
    }

    public async Task<CreatorResponseDTO> GetCreatorByIdAsync(long id)
    {
        CreatorModel model = await InvokeDAOMethod(() => dao.GetByIdAsync(id));
        return MakeResponseFromModel(model);
    }

    public async Task<CreatorResponseDTO> UpdateCreatorByIdAsync(long creatorId, CreatorRequestDTO dto)
    {
        CreatorModel model = MakeModelFromRequest(dto);
        model.Id = creatorId;
        CreatorModel result = await InvokeDAOMethod(() => dao.UpdateAsync(model));
        return MakeResponseFromModel(result);
    }

    private static CreatorModel MakeModelFromRequest(CreatorRequestDTO dto)
    {
        CreatorModel result = new();
        ShapeModelFromRequest(ref result, dto);
        return result;
    }

    private static void ShapeModelFromRequest(ref CreatorModel model, CreatorRequestDTO dto)
    {
        model.Id = dto.Id ?? 0;
        model.FirstName = dto.FirstName;
        model.LastName = dto.LastName;
        model.Login = dto.Login;
        model.Password = dto.Password;
    }

    private static CreatorResponseDTO MakeResponseFromModel(CreatorModel model)
    {
        return new CreatorResponseDTO()
        {
            Id = model.Id,
            FirstName = model.FirstName,
            LastName = model.LastName,
            Login = model.Login,
            Password = model.Password
        };
    }
}