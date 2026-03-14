using ArticleHouse.DAO;
using ArticleHouse.DAO.Models;
using ArticleHouse.Service.DTOs;
using ArticleHouse.Service.Exceptions;
using ArticleHouse.Service.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace ArticleHouse.Service.Implementations;

public class CreatorService : Service, ICreatorService
{
    private readonly ApplicationContext db;

    public CreatorService(ApplicationContext db)
    {
        this.db = db;
    }
    public async Task<CreatorResponseDTO[]> GetAllCreatorsAsync()
    {
        CreatorModel[] models = await db.Creators.ToArrayAsync();
        return [.. models.Select(MakeResponseFromModel)];
    }

    public async Task<CreatorResponseDTO> CreateCreatorAsync(CreatorRequestDTO dto)
    {
        CreatorModel model = MakeModelFromRequest(dto);
        await db.Creators.AddAsync(model);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
        return MakeResponseFromModel(model);
    }

    public async Task DeleteCreatorAsync(long id)
    {
        CreatorModel? model = await db.Creators.FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new ServiceObjectNotFoundException();
        }
        db.Creators.Remove(model);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
    }

    public async Task<CreatorResponseDTO> GetCreatorByIdAsync(long id)
    {
        CreatorModel? model = await db.Creators.FirstOrDefaultAsync(o => o.Id == id);
        if (null == model)
        {
            throw new ServiceObjectNotFoundException();
        }
        return MakeResponseFromModel(model);
    }

    public async Task<CreatorResponseDTO> UpdateCreatorByIdAsync(long creatorId, CreatorRequestDTO dto)
    {
        if (null == dto.Id)
        {
            throw new ServiceException();
        }
        CreatorModel? model = await db.Creators.FirstOrDefaultAsync(o => o.Id == dto.Id);
        if (null == model) {
            throw new ServiceObjectNotFoundException();
        }
        ShapeModelFromRequest(ref model, dto);
        await InvokeDAOMethod(() => db.SaveChangesAsync());
        return MakeResponseFromModel(model);
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