using lab1.Models.DTO.Requests;
using lab1.Models.Entities;
using lab1.Services.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace lab1.Data;

/// <summary>
/// Идемпотентное заполнение БД: выполняется только если ещё нет ни одного редактора.
/// </summary>
public static class DbSeeder
{
    public static async Task SeedAsync(
        AppDbContext db,
        INoticeService noticeService,
        CancellationToken cancellationToken = default)
    {
        if (await db.Editors.AnyAsync(cancellationToken).ConfigureAwait(false))
            return;

        var now = DateTime.UtcNow;

        var editor1 = new Editor
        {
            Login = "demo_editor",
            Password = "password12",
            FirstName = "Демо",
            LastName = "Яночкин"
        };
        var editor2 = new Editor
        {
            Login = "admin_seed",
            Password = "admin12345",
            FirstName = "Админ",
            LastName = "Системы"
        };
        db.Editors.AddRange(editor1, editor2);
        await db.SaveChangesAsync(cancellationToken).ConfigureAwait(false);

        var labelBug = new Label { Name = "bug" };
        var labelFeature = new Label { Name = "feature" };
        var labelDocs = new Label { Name = "docs" };
        db.Labels.AddRange(labelBug, labelFeature, labelDocs);
        await db.SaveChangesAsync(cancellationToken).ConfigureAwait(false);

        var issue1 = new Issue
        {
            Title = "Первая демо-задача",
            Content = "Описание первой задачи для демонстрации API и связей с метками.",
            EditorId = editor1.Id,
            Created = now,
            Modified = now
        };
        issue1.Labels.Add(labelBug);
        issue1.Labels.Add(labelFeature);

        var issue2 = new Issue
        {
            Title = "Вторая демо-задача",
            Content = "Вторая задача у другого редактора с меткой документации.",
            EditorId = editor2.Id,
            Created = now,
            Modified = now
        };
        issue2.Labels.Add(labelDocs);

        db.Issues.AddRange(issue1, issue2);
        await db.SaveChangesAsync(cancellationToken).ConfigureAwait(false);

        var noticeReq = new NoticeRequestTo
        {
            Id = 0,
            IssueId = issue1.Id,
            Content = "Начальное уведомление по первой задаче"
        };
        _ = await noticeService.CreateAsync(noticeReq, cancellationToken).ConfigureAwait(false);
    }
}
