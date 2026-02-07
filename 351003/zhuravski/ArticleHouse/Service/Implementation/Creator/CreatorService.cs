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
        return [];
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
}