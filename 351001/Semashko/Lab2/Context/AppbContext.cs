using System;
using System.Collections.Generic;
using Microsoft.EntityFrameworkCore;
using lab_1.Entities;

namespace lab_1.Context;

/// <summary>
/// Контекст базы данных для подключения к PostgreSQL через Entity Framework
/// Этот класс связывает C# код с таблицами в базе данных
/// </summary>
public partial class AppbContext : DbContext
{
    /// <summary>
    /// Пустой конструктор (используется для миграций)
    /// </summary>
    public AppbContext()
    {
        
    }

    /// <summary>
    /// Конструктор с параметрами подключения (используется при запуске приложения)
    /// Принимает настройки из Program.cs (строку подключения к БД)
    /// </summary>
    public AppbContext(DbContextOptions<AppbContext> options)
        : base(options)
    {
    }


    
    /// <summary>Таблица авторов в БД</summary>
    public virtual DbSet<TblAuthor> TblAuthors { get; set; }

    /// <summary>Таблица комментариев в БД</summary>
    public virtual DbSet<TblComment> TblComments { get; set; }

    /// <summary>Таблица меток в БД</summary>
    public virtual DbSet<TblMarker> TblMarkers { get; set; }

    /// <summary>Таблица рассказов в БД</summary>
    public virtual DbSet<TblStory> TblStories { get; set; }

    /// <summary>Таблица связи рассказов и меток (многие ко многим)</summary>
    public virtual DbSet<TblStoryMarker> TblStoryMarkers { get; set; }
    
    /// <summary>
    /// Этот метод настраивает как именно C# классы соответствуют таблицам в БД
    /// Вызывается автоматически при создании контекста
    /// </summary>
    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        // ============= НАСТРОЙКА ТАБЛИЦЫ АВТОРОВ =============
        modelBuilder.Entity<TblAuthor>(entity =>
        {
            // Первичный ключ (Id)
            entity.HasKey(e => e.Id).HasName("PK_i");

            // Имя таблицы "tblAuthors" в схеме "tbl"
            entity.ToTable("tblAuthors", "tbl");

            // Индексы для быстрого поиска
            entity.HasIndex(e => e.Id, "uniue_i").IsUnique();   // ID уникален
            entity.HasIndex(e => e.Login, "uniue_login").IsUnique(); // Логин уникален

            // Настройка колонок
            entity.Property(e => e.Id)
                .HasDefaultValueSql("'-1'::integer") // Значение по умолчанию = -1
                .HasColumnName("id");                 // Имя колонки в БД
                
            entity.Property(e => e.Firstname).HasColumnName("firstname");
            entity.Property(e => e.Lastname).HasColumnName("lastname");
            entity.Property(e => e.Login).HasColumnName("login");
            entity.Property(e => e.Password).HasColumnName("password");
        });

        // ============= НАСТРОЙКА ТАБЛИЦЫ КОММЕНТАРИЕВ =============
        modelBuilder.Entity<TblComment>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK1");

            entity.ToTable("tblComments", "tbl");

            entity.HasIndex(e => e.Id, "UN").IsUnique();

            entity.Property(e => e.Id)
                .ValueGeneratedNever() // ID не генерируется автоматически
                .HasColumnName("id");
                
            entity.Property(e => e.Content).HasColumnName("content");
            
            // Внешний ключ к таблице рассказов (к какому рассказу комментарий)
            entity.Property(e => e.StoryId).HasColumnName("storyId");
        });

        // ============= НАСТРОЙКА ТАБЛИЦЫ МЕТОК =============
        modelBuilder.Entity<TblMarker>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("Marker_pkey");

            entity.ToTable("tblMarker", "tbl");

            // Название метки должно быть уникальным
            entity.HasIndex(e => e.Name, "nameee").IsUnique();
            entity.HasIndex(e => e.Id, "u3").IsUnique();

            entity.Property(e => e.Id)
                .ValueGeneratedNever()
                .HasColumnName("id");
                
            entity.Property(e => e.Name).HasColumnName("name");
        });

        // ============= НАСТРОЙКА ТАБЛИЦЫ РАССКАЗОВ =============
        modelBuilder.Entity<TblStory>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("PK");

            entity.ToTable("tblStory", "tbl");

            // Индексы
            entity.HasIndex(e => e.Id, "uniue_i2").IsUnique();
            entity.HasIndex(e => e.Title, "u_title").IsUnique(); // Название должно быть уникальным
            
            entity.Property(e => e.Id)
                .HasDefaultValueSql("'-1'::integer")
                .HasColumnName("id");
                
            entity.Property(e => e.Title).HasColumnName("title");
            
            // Внешний ключ к автору
            entity.Property(e => e.AuthorId)
                .HasDefaultValueSql("'-1'::integer")
                .HasColumnName("authorId");
                
            entity.Property(e => e.Content).HasColumnName("content");
            entity.Property(e => e.Created).HasColumnName("created");
            entity.Property(e => e.Modified).HasColumnName("modified");
        });

        // ============= НАСТРОЙКА ТАБЛИЦЫ СВЯЗИ (РАССКАЗЫ-МЕТКИ) =============
        modelBuilder.Entity<TblStoryMarker>(entity =>
        {
            entity.HasKey(e => e.Id).HasName("iff");

            entity.ToTable("tblStoryMarker", "tbl");

            entity.Property(e => e.Id)
                .ValueGeneratedNever()
                .HasColumnName("id");
                
            // Внешний ключ к метке
            entity.Property(e => e.MarkerId).HasColumnName("markerId");
            
            // Внешний ключ к рассказу
            entity.Property(e => e.StoryId).HasColumnName("storyId");
        });

        OnModelCreatingPartial(modelBuilder);
    }

    /// <summary>
    /// Частичный метод для дополнительной настройки (можно добавить в отдельном файле)
    /// </summary>
    partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
}