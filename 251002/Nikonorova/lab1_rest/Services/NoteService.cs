using distcomp.DTOs;
using distcomp.Models;
using distcomp.Repositories;

namespace distcomp.Services
{
    public class NoteService
    {
        private readonly NoteRepository _noteRepository;

        public NoteService(NoteRepository noteRepository)
        {
            _noteRepository = noteRepository;
        }

        
        public NoteResponseTo CreateNote(NoteRequestTo noteRequest)
        {

            var note = new Note
            {
                ArticleId = noteRequest.ArticleId,
                Content = noteRequest.Content
            };

            var savedNote = _noteRepository.Save(note);
            return MapToNoteResponse(savedNote);
        }

        public NoteResponseTo GetNoteById(long id)
        {
            var note = _noteRepository.FindById(id);
            return note != null ? MapToNoteResponse(note) : null;
        }

        
        public List<NoteResponseTo> GetAllNotes()
        {
            var notes = _noteRepository.FindAll();
            var noteResponses = new List<NoteResponseTo>();
            foreach (var note in notes)
            {
                noteResponses.Add(MapToNoteResponse(note));
            }
            return noteResponses;
        }

        public NoteResponseTo UpdateNote(long id, NoteRequestTo noteRequest)
        {
            var note = _noteRepository.FindById(id); //checking that the post exists
            if (note == null)
                return null;

            note.ArticleId = noteRequest.ArticleId;
            note.Content = noteRequest.Content;

            var updatedNote = _noteRepository.Update(note);
            return MapToNoteResponse(updatedNote);
        }

        
        public bool DeleteNote(long id)
        {
            return _noteRepository.DeleteById(id);
        }

        //transforming post into DTO
        private NoteResponseTo MapToNoteResponse(Note note)
        {
            return new NoteResponseTo
            {
                Id = note.Id,
                ArticleId = note.ArticleId,
                Content = note.Content
            };
        }

    }
}
