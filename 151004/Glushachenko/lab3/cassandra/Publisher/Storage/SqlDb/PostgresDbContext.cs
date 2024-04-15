using Microsoft.EntityFrameworkCore;
using Publisher.Storage.Common;

namespace Publisher.Storage.SqlDb
{
    public class PostgresDbContext : DbStorage
    {
        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            //optionsBuilder.UseNpgsql("Host=localhost;Port=5432;Database=distcomp;Username=postgres;Password=postgres");
            optionsBuilder.UseNpgsql("Host=postgres;Port=5432;Database=distcomp;Username=postgres;Password=postgres");
        }
    }
}
