using Additions.Service;
using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Service.Implementations;

public class MarkService : Service, IMarkService
{
    private readonly IMarkDAO dao;
    public MarkService(IMarkDAO dao)
    {
        this.dao = dao;
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
    }

    public async Task<MarkResponseDTO[]> GetAllMarksAsync()
    {
        MarkModel[] daoModels = await InvokeDAOMethod(() => dao.GetAllAsync());
        return [.. daoModels.Select(MakeResponseFromModel)];
    }

    public async Task<MarkResponseDTO> GetMarkByIdAsync(long id)
    {
        MarkModel model = await InvokeDAOMethod(() => dao.GetByIdAsync(id));
        return MakeResponseFromModel(model);
    }

    public async Task<MarkResponseDTO> UpdateMarkByIdAsync(long id, MarkRequestDTO dto)
    {
        MarkModel model = MakeModelFromRequest(dto);
        model.Id = id;
        MarkModel result = await InvokeDAOMethod(() => dao.UpdateAsync(model));
        return MakeResponseFromModel(result);
    }

    public async Task<long[]> ReserveMarkIdsByNamesAsync(string[] names)
    {
        return await InvokeDAOMethod(() => dao.ReserveIdsByNamesAsync(names));
    }

    public async Task ReleaseLeftMarksByIdsAsync(long[] ids)
    {
        await InvokeDAOMethod(() => dao.ReleaseByIdsAsync(ids));
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