using System.Linq.Expressions;
using AutoMapper;
using lab1.Common.Sorting;
using lab1.Data;
using lab1.Errors;
using lab1.Models.DTO.Requests;
using lab1.Models.DTO.Responses;
using lab1.Models.Entities;
using lab1.Repositories.Interfaces;
using lab1.Services.Interfaces;
using Microsoft.EntityFrameworkCore;

namespace lab1.Services.Implementations;

public class IssueService : IIssueService
{
    private readonly IIssueRepository _issueRepo;
    private readonly IEntityRepository<Editor> _editorRepo;
    private readonly AppDbContext _db;
    private readonly IMapper _mapper;
    private readonly IDiscussionNoticeCleanup _noticeCleanup;

    public IssueService(
        IIssueRepository issueRepo,
        IEntityRepository<Editor> editorRepo,
        AppDbContext db,
        IMapper mapper,
        IDiscussionNoticeCleanup noticeCleanup)
    {
        _issueRepo = issueRepo;
        _editorRepo = editorRepo;
        _db = db;
        _mapper = mapper;
        _noticeCleanup = noticeCleanup;
    }

    public async Task<IssueResponseTo> CreateAsync(IssueRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);

        if (await _editorRepo.GetByIdAsync(request.EditorId, cancellationToken) == null)
            throw new ArgumentException("Editor not found");

        await EnsureTitleAvailableAsync(request.Title, excludeIssueId: null, cancellationToken);

        var issue = _mapper.Map<Issue>(request);
        issue.Id = 0;
        issue.Created = DateTime.UtcNow;
        issue.Modified = DateTime.UtcNow;

        await AttachIssueLabelsAsync(issue, request, cancellationToken);

        var created = await _issueRepo.AddAsync(issue, cancellationToken);
        return _mapper.Map<IssueResponseTo>(created);
    }

    public async Task<IssueResponseTo> GetByIdAsync(long id, CancellationToken cancellationToken = default)
    {
        var issue = await _issueRepo.GetByIdAsync(id, cancellationToken)
            ?? throw new KeyNotFoundException("Issue not found");

        return _mapper.Map<IssueResponseTo>(issue);
    }

    public async Task<PageResponseTo<IssueResponseTo>> GetPageAsync(
        int page,
        int size,
        string? sort,
        long? editorId,
        string? titleContains,
        CancellationToken cancellationToken = default)
    {
        Expression<Func<Issue, bool>>? filter = null;
        if (editorId.HasValue || !string.IsNullOrWhiteSpace(titleContains))
        {
            var eid = editorId;
            var title = titleContains;
            filter = i =>
                (!eid.HasValue || i.EditorId == eid.Value) &&
                (string.IsNullOrWhiteSpace(title) || i.Title.Contains(title!));
        }

        var order = IssueSortResolvers.Resolve(sort);
        var result = await _issueRepo.GetPagedAsync(filter, order, page, size, cancellationToken);

        return new PageResponseTo<IssueResponseTo>
        {
            Content = result.Content.Select(i => _mapper.Map<IssueResponseTo>(i)).ToList(),
            TotalElements = result.TotalElements,
            TotalPages = result.TotalPages,
            Number = result.Number,
            Size = result.Size
        };
    }

    public async Task<IReadOnlyList<IssueResponseTo>> GetAllAsync(CancellationToken cancellationToken = default)
    {
        var entities = await _issueRepo.GetAllAsync(cancellationToken);
        return entities.Select(i => _mapper.Map<IssueResponseTo>(i)).ToList();
    }

    public async Task<IssueResponseTo> UpdateAsync(IssueRequestTo request, CancellationToken cancellationToken = default)
    {
        Validate(request);

        var existing = await _issueRepo.GetByIdAsync(request.Id, cancellationToken)
            ?? throw new KeyNotFoundException("Issue not found");

        if (await _editorRepo.GetByIdAsync(request.EditorId, cancellationToken) == null)
            throw new ArgumentException("Editor not found");

        await EnsureTitleAvailableAsync(request.Title, request.Id, cancellationToken);

        var oldLabelIds = existing.Labels.Select(l => l.Id).ToList();

        existing.Title = request.Title;
        existing.Content = request.Content;
        existing.EditorId = request.EditorId;
        existing.Modified = DateTime.UtcNow;

        existing.Labels.Clear();
        await AttachIssueLabelsAsync(existing, request, cancellationToken);

        var updated = await _issueRepo.UpdateAsync(existing, cancellationToken);

        var newLabelIds = existing.Labels.Select(l => l.Id).ToHashSet();
        var removedLabelIds = oldLabelIds.Where(id => !newLabelIds.Contains(id)).ToList();
        await RemoveOrphanLabelsAsync(removedLabelIds, cancellationToken);

        return _mapper.Map<IssueResponseTo>(updated);
    }

    public async Task DeleteAsync(long id, CancellationToken cancellationToken = default)
    {
        var issue = await _issueRepo.GetByIdAsync(id, cancellationToken)
            ?? throw new KeyNotFoundException("Issue not found");

        var labelIds = issue.Labels.Select(l => l.Id).ToList();

        await _noticeCleanup.DeleteAllForIssueAsync(id, cancellationToken).ConfigureAwait(false);

        await _issueRepo.DeleteByIdAsync(id, cancellationToken).ConfigureAwait(false);

        await RemoveOrphanLabelsAsync(labelIds, cancellationToken);
    }

    private async Task EnsureTitleAvailableAsync(
        string title,
        long? excludeIssueId,
        CancellationToken cancellationToken)
    {
        Expression<Func<Issue, bool>> filter = excludeIssueId.HasValue
            ? i => i.Title == title && i.Id != excludeIssueId.Value
            : i => i.Title == title;

        var found = await _issueRepo.GetPagedAsync(filter, null, 0, 1, cancellationToken);
        if (found.TotalElements > 0)
            throw new IssueTitleAlreadyExistsException();
    }

    private async Task AttachIssueLabelsAsync(Issue issue, IssueRequestTo request, CancellationToken cancellationToken)
    {
        var rawNames = new List<string>();
        if (request.Labels != null)
            rawNames.AddRange(request.Labels);
        rawNames.AddRange(request.LabelNames);

        var names = rawNames
            .Select(n => n.Trim())
            .Where(n => n.Length > 0)
            .ToList();

        if (names.Count > 0)
        {
            if (names.Count != names.Distinct(StringComparer.Ordinal).Count())
                throw new ArgumentException("Duplicate label names");

            foreach (var name in names)
            {
                if (name.Length > 128)
                    throw new ArgumentException("Label name exceeds maximum length");

                var label = await _db.Labels.FirstOrDefaultAsync(l => l.Name == name, cancellationToken);
                if (label == null)
                {
                    label = new Label { Name = name };
                    await _db.Labels.AddAsync(label, cancellationToken);
                }

                issue.Labels.Add(label);
            }

            return;
        }

        await AttachLabelsByIdsAsync(issue, request.LabelIds, cancellationToken);
    }

    /// <summary>
    /// Удаляет из tbl_label метки, которые больше ни с одним issue не связаны (после delete/update issue).
    /// </summary>
    private async Task RemoveOrphanLabelsAsync(IReadOnlyList<long> candidateIds, CancellationToken cancellationToken)
    {
        if (candidateIds.Count == 0)
            return;

        foreach (var lid in candidateIds)
        {
            var stillUsed = await _db.Issues.AsNoTracking()
                .AnyAsync(i => i.Labels.Any(l => l.Id == lid), cancellationToken);
            if (stillUsed)
                continue;

            await _db.Labels.Where(l => l.Id == lid).ExecuteDeleteAsync(cancellationToken);
        }
    }

    private async Task AttachLabelsByIdsAsync(Issue issue, List<long> labelIds, CancellationToken cancellationToken)
    {
        if (labelIds.Count == 0)
            return;

        if (labelIds.Count != labelIds.Distinct().Count())
            throw new ArgumentException("Duplicate label ids");

        var set = labelIds.ToHashSet();
        var labels = await _db.Labels.Where(l => set.Contains(l.Id)).ToListAsync(cancellationToken);
        if (labels.Count != set.Count)
            throw new ArgumentException("One or more label ids are invalid");

        foreach (var label in labels)
            issue.Labels.Add(label);
    }

    private static void Validate(IssueRequestTo request)
    {
        if (string.IsNullOrWhiteSpace(request.Title) || request.Title.Length < 3)
            throw new ArgumentException("Title is too short");

        if (request.Title.Length > 64)
            throw new ArgumentException("Title exceeds maximum length");

        if (string.IsNullOrWhiteSpace(request.Content))
            throw new ArgumentException("Content must not be empty");

        if (request.EditorId <= 0)
            throw new ArgumentException("EditorId must be positive");
    }
}
