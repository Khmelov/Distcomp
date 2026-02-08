using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Service.Implementations;

public class CreatorService : Service, ICreatorService
{
    private readonly ILogger<CreatorService> logger;
    private readonly ICreatorDAO creatorDAO;

    public CreatorService(ILogger<CreatorService> logger, ICreatorDAO creatorDAO)
    {
        this.logger = logger;
        this.creatorDAO = creatorDAO;
    }
    public async Task<CreatorResponseDTO[]> GetAllCreatorsAsync()
    {
        CreatorModel[] daoModels = await InvokeDAOMethod(() => creatorDAO.GetAllAsync());
        return [.. daoModels.Select(MakeResponseFromModel)];
    }

    public async Task<CreatorResponseDTO> CreateCreatorAsync(CreatorRequestDTO dto)
    {
        CreatorModel model = MakeModelFromRequest(dto);
        CreatorModel result = await InvokeDAOMethod(() => creatorDAO.AddNewAsync(model));
        return MakeResponseFromModel(result);
    }

    public async Task DeleteCreatorAsync(long creatorId)
    {
        await InvokeDAOMethod(() => creatorDAO.DeleteAsync(creatorId));
    }

    public async Task<CreatorResponseDTO> GetCreatorByIdAsync(long creatorId)
    {
        CreatorModel model = await InvokeDAOMethod(() => creatorDAO.GetByIdAsync(creatorId));
        return MakeResponseFromModel(model);
    }

    public async Task<CreatorResponseDTO> UpdateCreatorByIdAsync(long creatorId, CreatorRequestDTO dto)
    {
        CreatorModel model = MakeModelFromRequest(dto);
        model.Id = creatorId;
        CreatorModel result = await InvokeDAOMethod(() => creatorDAO.UpdateAsync(model));
        return MakeResponseFromModel(result);
    }

    private static CreatorModel MakeModelFromRequest(CreatorRequestDTO dto)
    {
        return new CreatorModel()
        {
            Id = dto.Id ?? 0,
            FirstName = dto.FirstName,
            LastName = dto.LastName,
            Login = dto.Login,
            Password = dto.Password
        };
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