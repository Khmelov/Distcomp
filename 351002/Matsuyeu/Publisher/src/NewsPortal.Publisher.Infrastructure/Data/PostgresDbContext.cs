// src/Publisher/NewsPortal.Publisher.Infrastructure/Data/PostgresDbContext.cs
using Microsoft.EntityFrameworkCore;
using Publisher.src.NewsPortal.Publisher.Domain.Entities;

namespace Publisher.src.NewsPortal.Publisher.Infrastructure.Data
{
    public class PostgresDbContext : DbContext
    {
        public PostgresDbContext(DbContextOptions<PostgresDbContext> options)
            : base(options)
        {
        }

        // DbSet только для сущностей, которые остаются в PostgreSQL
        public DbSet<Creator> Creators { get; set; }
        public DbSet<News> News { get; set; }
        public DbSet<Mark> Marks { get; set; }

        // Note удаляем! Он будет в Cassandra через Discussion микросервис

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Конфигурация Creator
            modelBuilder.Entity<Creator>(entity =>
            {
                entity.ToTable("tbl_creator");
                entity.HasKey(e => e.Id);

                entity.Property(e => e.Id)
                    .HasColumnName("id")
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.Login)
                    .IsRequired()
                    .HasMaxLength(64)
                    .HasColumnName("login");

                entity.Property(e => e.Password)
                    .IsRequired()
                    .HasMaxLength(128)
                    .HasColumnName("password");

                entity.Property(e => e.FirstName)
                    .IsRequired()
                    .HasMaxLength(64)
                    .HasColumnName("firstname");

                entity.Property(e => e.LastName)
                    .IsRequired()
                    .HasMaxLength(64)
                    .HasColumnName("lastname");

                entity.HasIndex(e => e.Login)
                    .IsUnique()
                    .HasDatabaseName("ix_creator_login");

                entity.HasMany(e => e.News)
                    .WithOne(e => e.Creator)
                    .HasForeignKey(e => e.CreatorId)
                    .HasConstraintName("fk_news_creator")
                    .OnDelete(DeleteBehavior.Restrict);
            });

            // Конфигурация News (без связи с Notes)
            modelBuilder.Entity<News>(entity =>
            {
                entity.ToTable("tbl_news");
                entity.HasKey(e => e.Id);

                entity.Property(e => e.Id)
                    .HasColumnName("id")
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.CreatorId)
                    .IsRequired()
                    .HasColumnName("creator_id");

                entity.Property(e => e.Title)
                    .IsRequired()
                    .HasMaxLength(64)
                    .HasColumnName("title");

                entity.Property(e => e.Content)
                    .IsRequired()
                    .HasMaxLength(2048)
                    .HasColumnName("content");

                entity.Property(e => e.Created)
                    .IsRequired()
                    .HasColumnName("created")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.Property(e => e.Modified)
                    .IsRequired()
                    .HasColumnName("modified")
                    .HasDefaultValueSql("CURRENT_TIMESTAMP");

                entity.HasIndex(e => e.Title)
                    .IsUnique()
                    .HasDatabaseName("ix_news_title");

                entity.HasOne(e => e.Creator)
                    .WithMany(e => e.News)
                    .HasForeignKey(e => e.CreatorId)
                    .HasConstraintName("fk_news_creator")
                    .OnDelete(DeleteBehavior.Restrict);

                // Удаляем связь с Notes, так как они теперь в другом сервисе
                // entity.HasMany(e => e.Notes) - больше не нужно
            });

            // Конфигурация Mark
            modelBuilder.Entity<Mark>(entity =>
            {
                entity.ToTable("tbl_mark");
                entity.HasKey(e => e.Id);

                entity.Property(e => e.Id)
                    .HasColumnName("id")
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.Name)
                    .IsRequired()
                    .HasMaxLength(32)
                    .HasColumnName("name");

                entity.HasIndex(e => e.Name)
                    .IsUnique()
                    .HasDatabaseName("ix_mark_name");
            });

            // Связь многие-ко-многим между News и Mark
            modelBuilder.Entity<News>()
                .HasMany(e => e.Marks)
                .WithMany(e => e.News)
                .UsingEntity<Dictionary<string, object>>(
                    "tbl_news_mark",
                    j => j.HasOne<Mark>().WithMany().HasForeignKey("mark_id"),
                    j => j.HasOne<News>().WithMany().HasForeignKey("news_id"),
                    j =>
                    {
                        j.ToTable("tbl_news_mark");
                        j.HasKey("news_id", "mark_id");
                    });
        }
    }
}