using Domain.Entities;
using Microsoft.EntityFrameworkCore;

namespace Infrastructure;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
    }
    
    public DbSet<Author> Authors { get; set; } = null!;
    public DbSet<Issue> Issues { get; set; } = null!;
    public DbSet<Label> Labels { get; set; } = null!;
    public DbSet<Comment> Comments { get; set; } = null!;
    
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        
        modelBuilder.Entity<Issue>()
            .HasMany(i => i.Labels)
            .WithMany(l => l.Issues)
            .UsingEntity<Dictionary<string, object>>(
                "tbl_issue_label", 
                j => j.HasOne<Label>()
                    .WithMany()
                    .HasForeignKey("label_id")
                    .OnDelete(DeleteBehavior.Cascade),
                j => j.HasOne<Issue>()
                    .WithMany()
                    .HasForeignKey("issue_id")
                    .OnDelete(DeleteBehavior.Cascade)
            )
            ;
        
        modelBuilder.Entity<Author>()
            .HasIndex(a => a.Login)
            .IsUnique();

        modelBuilder.Entity<Label>()
            .HasIndex(l => l.Name)
            .IsUnique();
        
        modelBuilder.Entity<Comment>()
            .HasOne(c => c.Issue)
            .WithMany(i => i.Comments)
            .HasForeignKey(c => c.IssueId)
            .OnDelete(DeleteBehavior.Cascade);
        
        modelBuilder.Entity<Author>().HasData(new Author
        {
            Id = 1,
            Login = "alexander.shevchenko.bsuir@gmail.com",
            Password = "DefaultPassword123",
            Firstname = "Александр",
            Lastname = "Шевченко"
        });
    }
}