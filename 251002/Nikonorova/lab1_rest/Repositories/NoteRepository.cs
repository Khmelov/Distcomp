using distcomp.Models;
using System.Collections.Generic;
using System.Linq;

namespace distcomp.Repositories
{
    public class NoteRepository : ICrudRepository<Note, long>
    {
        private readonly Dictionary<long, Note> _notes = new();
        private long _idCounter = 0;

        public Note Save(Note note)
        {
            note.Id = ++_idCounter; //generating id
            _notes[note.Id] = note; //in-memory saving 
            return note;
        }

        public Note FindById(long id)
        {
            return _notes.TryGetValue(id, out var note) ? note : null;
        }

        public List<Note> FindAll()
        {
            return _notes.Values.ToList(); //getting a list of posts
        }

        public Note Update(Note note)
        {
            if (_notes.ContainsKey(note.Id))
            {
                _notes[note.Id] = note; //updating existing post
                return note;
            }
            return null; //if no post found
        }

        public bool DeleteById(long id)
        {
            return _notes.Remove(id); 
        }
    }
}
