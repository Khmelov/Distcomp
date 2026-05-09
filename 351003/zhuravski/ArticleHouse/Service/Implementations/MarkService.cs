using Additions.Cache.Interfaces;
using Additions.Service;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Service.Implementations;

public class MarkService : BasicService, IMarkService
{
    private readonly IMarkDAO dao;
    private readonly IDistributedCache cache;
    private readonly ILogger<MarkService> logger;

    public MarkService(IMarkDAO dao, IDistributedCache cache, ILogger<MarkService> logger)
    {
        this.dao = dao;
        this.cache = cache;
        this.logger = logger;
    }

    public async Task<MarkResponseDTO> CreateMarkAsync(MarkRequestDTO dto)
    {
        MarkModel model = MakeModelFromRequest(dto);
        MarkModel result = await InvokeDAOMethod(() => dao.AddNewAsync(model));
        return MakeResponseFromModel(result);
    }

    public async Task DeleteMarkAsync(long id)
    {
        await InvokeDAOMethod(() => dao.DeleteAsync(id));
        await cache.RemoveAsync($"mark:{id}");
    }

    public async Task<MarkResponseDTO[]> GetAllMarksAsync()
    {
        MarkModel[] daoModels = await InvokeDAOMethod(() => dao.GetAllAsync());
        return [.. daoModels.Select(MakeResponseFromModel)];
    }

    public async Task<MarkResponseDTO> GetMarkByIdAsync(long id)
    {
        var key = $"mark:{id}";
        
        return await cache.GetOrSetAsync(
            key,
            async () =>
            {
                MarkModel model = await InvokeDAOMethod(() => dao.GetByIdAsync(id));
                return MakeResponseFromModel(model);
            },
            TimeSpan.FromMinutes(10)
        );
    }

    public async Task<MarkResponseDTO> UpdateMarkByIdAsync(long id, MarkRequestDTO dto)
    {
        MarkModel model = MakeModelFromRequest(dto);
        model.Id = id;
        MarkModel result = await InvokeDAOMethod(() => dao.UpdateAsync(model));
        await cache.RemoveAsync($"mark:{id}");
        return MakeResponseFromModel(result);
    }

    private static MarkModel MakeModelFromRequest(MarkRequestDTO dto)
    {
        MarkModel result = new();
        ShapeModelFromRequest(ref result, dto);
        return result;
    }

    private static void ShapeModelFromRequest(ref MarkModel model, MarkRequestDTO dto)
    {
        model.Id = dto.Id ?? 0;
        model.Name = dto.Name;
    }

    private static MarkResponseDTO MakeResponseFromModel(MarkModel model)
    {
        return new MarkResponseDTO()
        {
            Id = model.Id,
            Name = model.Name
        };
    }
}