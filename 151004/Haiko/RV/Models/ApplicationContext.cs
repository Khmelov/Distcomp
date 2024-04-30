using Microsoft.EntityFrameworkCore;
using Npgsql;

namespace RV.Models
{
    public class ApplicationContext : DbContext
    {
        public DbSet<User> Users { get; set; } = null!;
        public DbSet<Tweet> News { get; set; } = null!;
        public DbSet<Note> Notes { get; set; } = null!;
        public DbSet<Sticker> Stickers { get; set; } = null!;
        public ApplicationContext(DbContextOptions<ApplicationContext> options)
        : base(options)
        { }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            var masterConnectionString = new NpgsqlConnectionStringBuilder();
            masterConnectionString.Host = "localhost";
            masterConnectionString.Port = 5432;
            masterConnectionString.Username = "postgres";
            masterConnectionString.Password = "Alex2405200417";
            masterConnectionString.Database = "distcomp";
            optionsBuilder.UseNpgsql(masterConnectionString.ConnectionString);
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder
                .Entity<Tweet>()
                .HasMany(c => c.Stickers)
                .WithMany(s => s.Tweets)
                .UsingEntity<TweetsSticker>(
                   j => j
                    .HasOne(pt => pt.Sticker)
                    .WithMany(t => t.TweetsStickers)
                    .HasForeignKey(pt => pt.stickerId),
                j => j
                    .HasOne(pt => pt.Tweet)
                    .WithMany(p => p.TweetsStickers)
                    .HasForeignKey(pt => pt.tweetId),
                j =>
                {
                    j.HasKey(t => t.id);
                    j.ToTable("TweetsSticker");
                });

            modelBuilder.Entity<User>()
                .ToTable(t => t.HasCheckConstraint("len(login) < 2", "LENGTH(\"login\") > 2"));
            modelBuilder.Entity<User>()
                .ToTable(t => t.HasCheckConstraint("len(password) < 8", "LENGTH(\"password\") > 8"));
            modelBuilder.Entity<User>()
                .ToTable(t => t.HasCheckConstraint("len(firstName) < 2", "LENGTH(\"firstName\") > 2"));
            modelBuilder.Entity<User>()
                .ToTable(t => t.HasCheckConstraint("len(lastName) < 2", "LENGTH(\"lastName\") > 2"));

            modelBuilder.Entity<Tweet>()
                .ToTable(t => t.HasCheckConstraint("len(title) < 2", "LENGTH(\"title\") > 2"));
            modelBuilder.Entity<Tweet>()
                .ToTable(t => t.HasCheckConstraint("len(content) < 4", "LENGTH(\"content\") > 4"));

            modelBuilder.Entity<Note>()
                .ToTable(t => t.HasCheckConstraint("len(content) < 2", "LENGTH(\"content\") > 2"));

            modelBuilder.Entity<Sticker>()
                .ToTable(t => t.HasCheckConstraint("len(name) < 2", "LENGTH(\"name\") > 2"));

            modelBuilder.Entity<User>()
                .HasIndex(p => p.login).IsUnique();
            modelBuilder.Entity<Tweet>()
                .HasIndex(p => p.title).IsUnique();
            modelBuilder.Entity<Sticker>()
                .HasIndex(p => p.name).IsUnique();
        }
    } 
}
