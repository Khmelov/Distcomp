// src/NewsPortal.Infrastructure/Data/DesignTimeDbContextFactory.cs
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using NewsPortal.Data;

namespace NewsPortal.Infrastructure.Data;

public class DesignTimeDbContextFactory : IDesignTimeDbContextFactory<AppDbContext>
{
    public AppDbContext CreateDbContext(string[] args)
    {
        var optionsBuilder = new DbContextOptionsBuilder<AppDbContext>();

        // Строка подключения для миграций
        optionsBuilder.UseNpgsql("Host=localhost;Port=5432;Database=distcomp;Username=postgres;Password=postgres");

        return new AppDbContext(optionsBuilder.Options);
    }
}