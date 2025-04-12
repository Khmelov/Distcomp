using Microsoft.EntityFrameworkCore;

public class AppDbContext : DbContext
{
    public DbSet<Editor> Editors { get; set; }
    public DbSet<Label> Label { get; set; }
    public DbSet<Note> Note { get; set; }
    public DbSet<Story> Story { get; set; }

    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
    {
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<Editor>(entity =>
        {
            entity.ToTable("tbl_editor");
            entity.HasKey(e => e.id);
            entity.Property(e => e.login).IsRequired().HasMaxLength(64);
            entity.Property(e => e.password).IsRequired().HasMaxLength(128);
            entity.Property(e => e.firstname).IsRequired().HasMaxLength(64);
            entity.Property(e => e.lastname).IsRequired().HasMaxLength(64);
        });

        modelBuilder.Entity<Story>(entity =>
        {
            entity.ToTable("tbl_story");
            entity.HasKey(e => e.id);
            entity.Property(e => e.Title).IsRequired().HasMaxLength(64);
            entity.Property(e => e.Content).IsRequired().HasMaxLength(2048);
            entity.Property(e => e.Created).IsRequired();
            entity.Property(e => e.Modified).IsRequired();
            entity.Property(e => e.EditorId).HasColumnName("editor_id");

            entity.HasOne<Editor>()    
                .WithMany()
                .HasForeignKey(e => e.EditorId)
                .OnDelete(DeleteBehavior.Cascade); 
        });

        modelBuilder.Entity<Label>(entity =>
        {
            entity.ToTable("tbl_label");
            entity.HasKey(e => e.id);
            entity.Property(e => e.Name).IsRequired().HasMaxLength(32);
        });

        modelBuilder.Entity<Note>(entity =>
        {
            entity.ToTable("tbl_note");
            entity.HasKey(e => e.id);;
            entity.Property(e => e.Content).IsRequired().HasMaxLength(2048);
            entity.Property(e => e.storyId).HasColumnName("story_id");

            entity.HasOne<Story>()
                .WithMany()
                .HasForeignKey(e => e.storyId)
                .OnDelete(DeleteBehavior.Cascade); 
        });
    }
}