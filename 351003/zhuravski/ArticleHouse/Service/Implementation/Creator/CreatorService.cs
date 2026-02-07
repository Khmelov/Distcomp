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
        try
        {
            CreatorModel[] daoModels = await creatorDAO.GetAllCreators();
            CreatorResponseDTO[] result = new CreatorResponseDTO[daoModels.Length];
            for (int i = 0; i < daoModels.Length; i++)
            {
                result[i] = MakeResponseFromModel(daoModels[i]);
            }
            return result;
        }
        catch (DAOException e)
        {
            throw new ServiceException(e.Message);
        }
    }

    public async Task<CreatorResponseDTO> CreateCreatorAsync(CreatorRequestDTO dto)
    {
        CreatorModel model = MakeModelFromRequest(dto);
        CreatorModel result = await creatorDAO.AddNewCreator(model);
        return MakeResponseFromModel(result);
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
}