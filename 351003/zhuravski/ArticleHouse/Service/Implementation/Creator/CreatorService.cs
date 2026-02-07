using ArticleHouse.DAO.CreatorDAO;

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
        CreatorModel[] daoModels = await creatorDAO.GetAllCreators();
        CreatorResponseDTO[] result = new CreatorResponseDTO[daoModels.Length];
        for (int i = 0; i < daoModels.Length; i++)
        {
            result[i] = MakeResponseFromModel(daoModels[i]);
        }
        return result;
    }

    public async Task<CreatorResponseDTO> CreateCreatorAsync(CreatorRequestDTO dto)
    {
        return new CreatorResponseDTO()
        {
            Password = dto.Password,
            FirstName = dto.FirstName,
            Login = dto.Login,
            LastName = dto.LastName,
            Id = 666
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