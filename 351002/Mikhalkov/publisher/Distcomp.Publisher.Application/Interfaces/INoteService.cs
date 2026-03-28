using Distcomp.Application.DTOs;

namespace Distcomp.Application.Interfaces
{
    public interface INoteService
    {
        NoteResponseTo Create(NoteRequestTo request);
        NoteResponseTo? GetById(long id);
        IEnumerable<NoteResponseTo> GetAll();
        NoteResponseTo Update(long id, NoteRequestTo request);
        bool Delete(long id);
    }
}