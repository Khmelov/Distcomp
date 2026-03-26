using Microsoft.EntityFrameworkCore;
using RV_Kisel_lab2_Task320.Models.Entities;

namespace RV_Kisel_lab2_Task320.Data;

public class AppDbContext : DbContext {
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) {}
    public DbSet<Creator> Creators { get; set; }
    public DbSet<News> News { get; set; }
    public DbSet<Label> Labels { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder) {
        // Жестко указываем названия колонок для связи Многие-ко-многим
        modelBuilder.Entity<News>()
            .HasMany(n => n.Labels)
            .WithMany(l => l.News)
            .UsingEntity<Dictionary<string, object>>(
                "tbl_news_label",
                j => j.HasOne<Label>().WithMany().HasForeignKey("label_id"),
                j => j.HasOne<News>().WithMany().HasForeignKey("news_id")
            );
    }
}