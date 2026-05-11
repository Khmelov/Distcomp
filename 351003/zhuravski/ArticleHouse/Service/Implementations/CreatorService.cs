using Additions.Cache.Interfaces;
using Additions.Service;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Service.Implementations;

public class CreatorService : BasicService, ICreatorService
{
    private readonly ICreatorDAO dao;
    private readonly IDistributedCache cache;
    private readonly ILogger<CreatorService> logger;

    public CreatorService(ICreatorDAO dao, IDistributedCache cache, ILogger<CreatorService> logger)
    {
        this.dao = dao;
        this.cache = cache;
        this.logger = logger;
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
        await cache.RemoveAsync($"creator:{id}");
    }

    public async Task<CreatorResponseDTO> GetCreatorByIdAsync(long id)
    {
        var key = $"creator:{id}";
        
        return await cache.GetOrSetAsync(
            key,
            async () =>
            {
                CreatorModel model = await InvokeDAOMethod(() => dao.GetByIdAsync(id));
                return MakeResponseFromModel(model);
            },
            TimeSpan.FromMinutes(10)
        );
    }

    public async Task<CreatorResponseDTO> UpdateCreatorByIdAsync(long creatorId, CreatorRequestDTO dto)
    {
        CreatorModel origin = await InvokeLowerMethod(() => dao.GetByIdAsync(creatorId));
        CreatorModel model = MakeModelFromRequest(dto);
        model.Id = creatorId;
        model.Role = origin.Role;
        CreatorModel result = await InvokeLowerMethod(() => dao.UpdateAsync(model));
        await cache.RemoveAsync($"creator:{creatorId}");
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
        model.Role = CreatorModel.CUSTOMER_ROLE;
    }

    private static CreatorResponseDTO MakeResponseFromModel(CreatorModel model)
    {
        return new CreatorResponseDTO()
        {
            Id = model.Id,
            FirstName = model.FirstName,
            LastName = model.LastName,
            Login = model.Login,
            Role = model.Role
        };
    }
}