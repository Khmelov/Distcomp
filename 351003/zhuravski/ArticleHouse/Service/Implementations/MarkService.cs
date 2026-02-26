using ArticleHouse.DAO.Interfaces;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Interfaces;

namespace ArticleHouse.Service.Implementations;

public class MarkService : Service, IMarkService
{
    private readonly IMarkDAO markDAO;
    public MarkService(IMarkDAO markDAO)
    {
        this.markDAO = markDAO;
    }
    public async Task<MarkResponseDTO> CreateMarkAsync(MarkRequestDTO dto)
    {
        MarkModel model = MakeModelFromRequest(dto);
        MarkModel result = await InvokeDAOMethod(() => markDAO.AddNewAsync(model));
        return MakeResponseFromModel(result);
    }

    public async Task DeleteMarkAsync(long id)
    {
        await InvokeDAOMethod(() => markDAO.DeleteAsync(id));
    }

    public async Task<MarkResponseDTO[]> GetAllMarksAsync()
    {
        MarkModel[] daoModels = await InvokeDAOMethod(() => markDAO.GetAllAsync());
        return [.. daoModels.Select(MakeResponseFromModel)];
    }

    public async Task<MarkResponseDTO> GetMarkByIdAsync(long id)
    {
        MarkModel model = await InvokeDAOMethod(() => markDAO.GetByIdAsync(id));
        return MakeResponseFromModel(model);
    }

    public async Task<MarkResponseDTO> UpdateMarkByIdAsync(long id, MarkRequestDTO dto)
    {
        MarkModel model = MakeModelFromRequest(dto);
        model.Id = id;
        MarkModel result = await InvokeDAOMethod(() => markDAO.UpdateAsync(model));
        return MakeResponseFromModel(result);
    }

    private static MarkModel MakeModelFromRequest(MarkRequestDTO dto)
    {
        return new MarkModel()
        {
            Id = dto.Id ?? 0,
            Name = dto.Name
        };
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