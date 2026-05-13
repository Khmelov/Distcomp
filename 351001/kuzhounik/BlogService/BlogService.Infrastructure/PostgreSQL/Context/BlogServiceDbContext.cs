using BlogService.Domain.Entities;
using Microsoft.EntityFrameworkCore;

namespace BlogService.Infrastructure.PostgreSQL.Context;

public class BlogServiceDbContext : DbContext
{
    public BlogServiceDbContext(DbContextOptions<BlogServiceDbContext> options) : base(options) { }
    
    public DbSet<Comment<long>> Comment => Set<Comment<long>>();
    public DbSet<Sticker<long>> Sticker => Set<Sticker<long>>();
    public DbSet<Story<long>> Story => Set<Story<long>>();
    public DbSet<User<long>> User => Set<User<long>>();

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        //modelBuilder.HasDefaultSchema("distcomp");
        base.OnModelCreating(modelBuilder);
        
        modelBuilder.HasDefaultSchema("distcomp");
        
        foreach (var entity in modelBuilder.Model.GetEntityTypes())
        {
            var currentName = entity.GetTableName();
            if (currentName != null && !currentName.StartsWith("tbl_"))
            {
                entity.SetTableName($"tbl_{currentName.ToLower()}");
            }
            //
            // var currentTableName = entity.GetTableName();
            // if (currentTableName != null && !currentTableName.StartsWith("tbl_"))
            // {
            //     entity.SetTableName($"tbl_{currentTableName.ToLower()}");
            // }
            // else if (currentTableName != null)
            // {
            //     entity.SetTableName(currentTableName.ToLower());
            // }
            //
            // // 2. ВСЕ колонки (включая ID и внешние ключи) в нижний регистр
            // foreach (var property in entity.GetProperties())
            // {
            //     // Это принудительно сделает ID -> id, Login -> login и т.д.
            //     property.SetColumnName(property.Name.ToLower());
            // }
            //
            // // 3. Первичные ключи (Primary Keys)
            // foreach (var key in entity.GetKeys())
            // {
            //     key.SetName(key.GetName()?.ToLower());
            // }
            //
            // // 4. Внешние ключи (Foreign Keys)
            // foreach (var foreignKey in entity.GetForeignKeys())
            // {
            //     foreignKey.SetConstraintName(foreignKey.GetConstraintName()?.ToLower());
            // }
            //
            // // 5. Индексы
            // foreach (var index in entity.GetIndexes())
            // {
            //     index.SetDatabaseName(index.GetDatabaseName()?.ToLower());
            // }
        }
        
        modelBuilder.Entity<User<long>>().HasIndex(u => u.Login).IsUnique();
        modelBuilder.Entity<Story<long>>().HasIndex(u => u.Title).IsUnique();
        
        // One-to-Many Story -> User
        modelBuilder.Entity<Story<long>>()
            .HasOne(s => s.User)
            .WithMany(u => u.Stories)
            .HasForeignKey(s => s.UserID);
        
        // One-to-Many Comment -> Story
        modelBuilder.Entity<Comment<long>>()
            .HasOne(s => s.Story)
            .WithMany(u => u.Comments)
            .HasForeignKey(s => s.StoryID);

        // Many-to-Many Story <-> Sticker
        modelBuilder.Entity<Story<long>>()
            .HasMany(s => s.Stickers)
            .WithMany(st => st.Stories)
            .UsingEntity<Dictionary<string, object>>(
                "tbl_story_sticker", // Имя таблицы в нижнем регистре
                j => j.HasOne<Sticker<long>>().WithMany().HasForeignKey("sticker_id"),
                j => j.HasOne<Story<long>>().WithMany().HasForeignKey("story_id"),
                j => 
                {
                    j.ToTable("tbl_story_sticker"); // Принудительно устанавливаем имя
                }
            );
    }
}