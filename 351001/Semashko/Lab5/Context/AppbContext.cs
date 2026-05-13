using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using lab_1.Entities;

namespace lab_1.Context;

public partial class AppbContext : DbContext
{
    public AppbContext()
    {
        
    }

    public AppbContext(DbContextOptions<AppbContext> options)
        : base(options)
    {
    }

    public virtual DbSet<TblAuthor> TblAuthors { get; set; }

    public virtual DbSet<TblComment> TblComments { get; set; }

    public virtual DbSet<TblMarker> TblMarkers { get; set; }

    public virtual DbSet<TblStory> TblStories { get; set; }

    public virtual DbSet<TblStoryMarker> TblStoryMarkers { get; set; }
    
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<TblAuthor>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK_i");

            entity.ToTable("tblAuthors", "tbl");

            entity.HasIndex(e => e.Id, "uniue_i").IsUnique();

            entity.HasIndex(e => e.Login, "uniue_login").IsUnique();

            entity.Property(e => e.Id)
                .HasDefaultValueSql("'-1'::integer")
                .HasColumnName("id");
            entity.Property(e => e.Firstname).HasColumnName("firstname");
            entity.Property(e => e.Lastname).HasColumnName("lastname");
            entity.Property(e => e.Login).HasColumnName("login");
            entity.Property(e => e.Password).HasColumnName("password");
        });

        modelBuilder.Entity<TblComment>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK1");

            entity.ToTable("tblComments", "tbl");

            entity.HasIndex(e => e.Id, "UN").IsUnique();

            entity.Property(e => e.Id)
                .ValueGeneratedNever()
                .HasColumnName("id");
            entity.Property(e => e.Content).HasColumnName("content");
            entity.Property(e => e.StoryId).HasColumnName("storyId");
        });

        modelBuilder.Entity<TblMarker>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("Marker_pkey");

            entity.ToTable("tblMarker", "tbl");

            entity.HasIndex(e => e.Name, "nameee").IsUnique();

            entity.HasIndex(e => e.Id, "u3").IsUnique();

            entity.Property(e => e.Id)
                .ValueGeneratedNever()
                .HasColumnName("id");
            entity.Property(e => e.Name).HasColumnName("name");
        });

        modelBuilder.Entity<TblStory>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK");

            entity.ToTable("tblStory", "tbl");

            entity.HasIndex(e => e.Id, "uniue_i2").IsUnique();
            entity.HasIndex(e => e.Title, "u_title").IsUnique();
            entity.Property(e => e.Id)
                .HasDefaultValueSql("'-1'::integer")
                .HasColumnName("id");
            entity.Property(e => e.Title).HasColumnName("title");
            entity.Property(e => e.AuthorId)
                .HasDefaultValueSql("'-1'::integer")
                .HasColumnName("authorId");
            entity.Property(e => e.Content).HasColumnName("content");
            entity.Property(e => e.Created).HasColumnName("created");
            entity.Property(e => e.Modified).HasColumnName("modified");
        });

        modelBuilder.Entity<TblStoryMarker>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("iff");

            entity.ToTable("tblStoryMarker", "tbl");

            entity.Property(e => e.Id)
                .ValueGeneratedNever()
                .HasColumnName("id");
            entity.Property(e => e.MarkerId).HasColumnName("markerId");
            entity.Property(e => e.StoryId).HasColumnName("storyId");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}
