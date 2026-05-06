

using Microsoft.EntityFrameworkCore;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<Writer> Writers { get; set; } 
    public DbSet<Story> Stories { get; set; }
    public DbSet<Label> Labels { get; set; }
   // public DbSet<Comment> Comments { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.HasDefaultSchema("distcomp");

        modelBuilder.Entity<Story>()
            .HasMany(s => s.Labels)
            .WithMany(l => l.Stories)
            .UsingEntity(j => j.ToTable("tbl_story_label", "distcomp"));

        modelBuilder.Entity<Story>()
            .HasOne(s => s.Writer)
            .WithMany(w => w.Stories)
            .HasForeignKey(s => s.WriterId);

       /* modelBuilder.Entity<Comment>()
            .HasOne(c => c.Story)
            .WithMany(s => s.Comments)
            .HasForeignKey(c => c.StoryId);*/
    }
}