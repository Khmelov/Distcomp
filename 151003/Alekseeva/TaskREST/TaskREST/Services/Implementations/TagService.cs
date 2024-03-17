using Microsoft.EntityFrameworkCore;
using TaskREST.Dto.Request.CreateTo;
using TaskREST.Dto.Request.UpdateTo;
using TaskREST.Dto.Response;
using TaskREST.Exceptions;
using TaskREST.Services.Interfaces;
using TaskREST.Storage;
using TagMapper = TaskREST.Mappers.TagMapper;

namespace TaskREST.Services.Implementations;

public sealed class TagService(AppDbContext context) : ITagService
{
    public async Task<TagResponseTo> GetTagById(long id)
    {
        var tag = await context.Tags.FindAsync(id);
        if (tag == null) throw new EntityNotFoundException($"Tag with id = {id} doesn't exist.");

        return TagMapper.Map(tag);
    }

    public async Task<IEnumerable<TagResponseTo>> GetTags()
    {
        return TagMapper.Map(await context.Tags.ToListAsync());
    }

    public async Task<TagResponseTo> CreateTag(CreateTagRequestTo createTagRequestTo)
    {
        var tag = TagMapper.Map(createTagRequestTo);
        await context.Tags.AddAsync(tag);
        await context.SaveChangesAsync();
        return TagMapper.Map(tag);
    }

    public async Task DeleteTag(long id)
    {
        var tag = await context.Tags.FindAsync(id);
        if (tag == null) throw new EntityNotFoundException($"Tag with id = {id} doesn't exist.");

        context.Tags.Remove(tag);
        await context.SaveChangesAsync();
    }

    public async Task<TagResponseTo> UpdateTag(UpdateTagRequestTo modifiedTag)
    {
        var tag = await context.Tags.FindAsync(modifiedTag.Id);
        if (tag == null) throw new EntityNotFoundException($"Tag with id = {modifiedTag.Id} doesn't exist.");

        context.Entry(tag).State = EntityState.Modified;

        tag.Id = modifiedTag.Id;
        tag.Name = modifiedTag.Name;

        await context.SaveChangesAsync();
        return TagMapper.Map(tag);
    }
}