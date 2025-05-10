using Publisher.Data;
using Publisher.Models;
using Publisher.Repositories.Interfaces;

namespace Publisher.Repositories.Implementations;

public class DatabaseStickerRepository : BaseDatabaseRepository<Sticker>, IStickerRepository
{
    public DatabaseStickerRepository(AppDbContext context) : base(context)
    {
    }
}