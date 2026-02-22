using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public class NoteService
    : BaseService<Note,NoteResponseTo,NoteRequestTo>
{
    public NoteService(IRepository<Note> repository, IMapper mapper) : base(repository, mapper)
    {
        
    }
}