using ArticleHouse.DAO.CreatorDAO;
using ArticleHouse.DAO.Exceptions;
using ArticleHouse.Service.Exceptions;

namespace ArticleHouse.Service.CreatorService;

public class CreatorService : ICreatorService
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
        CreatorModel[] daoModels = await InvokeDAOMethod(() => creatorDAO.GetAllCreatorsAsync());
        return [.. daoModels.Select(MakeResponseFromModel)];
    }

    public async Task<CreatorResponseDTO> CreateCreatorAsync(CreatorRequestDTO dto)
    {
        CreatorModel model = MakeModelFromRequest(dto);
        CreatorModel result = await InvokeDAOMethod(() => creatorDAO.AddNewCreatorAsync(model));
        return MakeResponseFromModel(result);
    }

    public async Task DeleteCreatorAsync(long creatorId)
    {
        await InvokeDAOMethod(() => creatorDAO.DeleteCreatorAsync(creatorId));
    }

    private static CreatorModel MakeModelFromRequest(CreatorRequestDTO dto)
    {
        return new CreatorModel()
        {
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

    private static async Task InvokeDAOMethod(Func<Task> call)
    {
        try
        {
            await call();
        }
        catch (DAOException e)
        {
            HandleDAOException(e);
        }
    }

    private static async Task<T> InvokeDAOMethod<T>(Func<Task<T>> call)
    {
        try
        {
            return await call();
        }
        catch (DAOException e)
        {
            HandleDAOException(e);
            return default!;
        }
    }

    private static void HandleDAOException(DAOException e)
    {
        if (e is DAOObjectNotFoundException)
        {
            throw new ServiceObjectNotFoundException(e.Message);
        }
        throw new ServiceException(e.Message);
    }
}