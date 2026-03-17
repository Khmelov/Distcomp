using ArticleHouse.DAO;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Exceptions;
using ArticleHouse.Service.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.Service.Implementations;

public class MarkService : Service, IMarkService
{
    private readonly ApplicationContext db;
    public MarkService(ApplicationContext db)
    {
        this.db = db;
    }
    public async Task<MarkResponseDTO> CreateMarkAsync(MarkRequestDTO dto)
    {
        MarkModel model = MakeModelFromRequest(dto);
        await db.Marks.AddAsync(model);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
        return MakeResponseFromModel(model);
    }

    public async Task DeleteMarkAsync(long id)
    {
        MarkModel? model = await db.Marks.FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new ServiceObjectNotFoundException();
        }
        db.Marks.Remove(model);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
    }

    public async Task<MarkResponseDTO[]> GetAllMarksAsync()
    {
        MarkModel[] models = await db.Marks.ToArrayAsync();
        return [.. models.Select(MakeResponseFromModel)];
    }

    public async Task<MarkResponseDTO> GetMarkByIdAsync(long id)
    {
        MarkModel? model = await db.Marks.FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new ServiceObjectNotFoundException();
        }
        return MakeResponseFromModel(model);
    }

    public async Task<MarkResponseDTO> UpdateMarkByIdAsync(long id, MarkRequestDTO dto)
    {
        if (null == dto.Id)
        {
            throw new ServiceException();
        }
        MarkModel? model = await db.Marks.FirstOrDefaultAsync(o => o.Id == dto.Id);
        if (null == model) {
            throw new ServiceObjectNotFoundException();
        }
        ShapeModelFromRequest(ref model, dto);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
        return MakeResponseFromModel(model);
    }

    public async Task<long[]> ReserveMarkIdsByNamesAsync(string[] names)
    {
        IEnumerable<string> marks = names.Distinct();
        List<MarkModel> markModels = await db.Marks.Where(m => marks.Contains(m.Name)).ToListAsync();
        HashSet<string> foundMarks = [.. markModels.Select(m => m.Name)];
        string[] missingMarks = [.. marks.Where(m => !foundMarks.Contains(m))];
        foreach (string missing in missingMarks)
        {
            MarkModel newModel = new()
            {
                Name = missing
            };
            markModels.Add(newModel);
            await db.Marks.AddAsync(newModel);
        }
        await db.SaveChangesAsync();
        return [.. markModels.Select(m => m.Id)];
    }

    public async Task ReleaseLeftMarksByIdsAsync(long[] ids)
    {
        if (ids.Length > 0)
        {
            await db.Marks
                    .Where(m => ids.Contains(m.Id))
                    .Where(m => !db.ArticleMarks.Any(m2 => m2.MarkId == m.Id))
                    .ExecuteDeleteAsync();
        }
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