using AutoMapper;
using Domain.Models;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Threading.Tasks;

namespace Infrastructe.ApplicationDbContext
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options)
        : base(options)
        {
        }

        public DbSet<Author> Editor => Set<Author>();
        public DbSet<Issue> Note => Set<Issue>();
        public DbSet<Comment> Story => Set<Comment>();
        public DbSet<Marker> Tag => Set<Marker>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // 1. Author
            modelBuilder.Entity<Author>(entity =>
            {
                entity.ToTable("tbl_author");
                entity.HasKey(e => e.id);

                entity.Property(a => a.id)
                    .ValueGeneratedOnAdd();

                // Уникальный логин (черная точка на схеме)
                entity.HasIndex(e => e.login).IsUnique();

                entity.Property(e => e.login).IsRequired().HasMaxLength(64);
                entity.Property(e => e.password).IsRequired().HasMaxLength(128);
                entity.Property(e => e.firstname).IsRequired().HasMaxLength(64);
                entity.Property(e => e.lastname).IsRequired().HasMaxLength(64);
            });

            // 2. Issue
            modelBuilder.Entity<Issue>(entity =>
            {
                entity.ToTable("tbl_issue");
                entity.HasKey(e => e.id);

                entity.Property(a => a.id)
                    .ValueGeneratedOnAdd();

                entity.HasIndex(i => i.title)
                    .IsUnique();

                entity.Property(e => e.title).IsRequired().HasMaxLength(64);
                entity.Property(e => e.content).IsRequired().HasMaxLength(2048);
                entity.Property(e => e.created).IsRequired();
                entity.Property(e => e.modified).IsRequired();
                entity.Property(e => e.authorId).HasColumnName("author_id");

                // Связь с автором (один-ко-многим)
                entity.HasOne(d => d.author)
                      .WithMany(p => p.issues)
                      .HasForeignKey(d => d.authorId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // 3. Comment
            modelBuilder.Entity<Comment>(entity =>
            {
                entity.ToTable("tbl_comment");
                entity.HasKey(e => e.id);
                entity.Property(e => e.content).IsRequired().HasMaxLength(2048);

                entity.Property(a => a.id)
                    .ValueGeneratedOnAdd();

                // Связь с задачей (один-ко-многим)
                entity.HasOne(d => d.issue)
                      .WithMany(p => p.comments)
                      .HasForeignKey(d => d.issueId)
                      .OnDelete(DeleteBehavior.Cascade);
            });

            // 4. Marker
            modelBuilder.Entity<Marker>(entity =>
            {
                entity.Property(a => a.id)
                    .ValueGeneratedOnAdd();

                entity.ToTable("tbl_marker");
                entity.HasKey(e => e.id);

                // Уникальное имя (черная точка на схеме)
                entity.HasIndex(e => e.name).IsUnique();
                entity.Property(e => e.name).IsRequired().HasMaxLength(32);
            });

            // 5. Настройка связи МНОГИЕ-КО-МНОГИМ (Issue <-> Marker)
            // Самого класса IssueMarker в Domain нет, но здесь мы описываем таблицу для неё
            modelBuilder.Entity<Issue>()
                .HasMany(i => i.markers)
                .WithMany(m => m.issues)
                .UsingEntity<Dictionary<string, object>>(
                    "IssueMarker", // Имя таблицы в БД
                    j => j.HasOne<Marker>().WithMany().HasForeignKey("markerId"),
                    j => j.HasOne<Issue>().WithMany().HasForeignKey("issueId"),
                    j =>
                    {
                        // Добавляем поле 'id' для таблицы связи, как на схеме
                        j.Property<long>("id").ValueGeneratedOnAdd();
                        j.HasKey("id");
                    });
        }
    }
}
