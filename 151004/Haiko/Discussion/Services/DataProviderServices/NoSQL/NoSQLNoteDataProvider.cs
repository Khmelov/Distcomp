using Discussion.Models;
using Discussion.Repositories;
using Discussion.Views.DTO;
using AutoMapper;

namespace Discussion.Services.DataProviderServices.SQL
{
    public class NoSQLNoteDataProvider : INoteDataProvider
    {
        private IRepository<Note> _repository;
        private IMapper _mapper;

        public NoSQLNoteDataProvider(IRepository<Note> repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }
        public NoteDTO CreateNote(NoteAddDTO item)
        {
            Note n = _mapper.Map<Note>(item);
            n.country = "Belarus";
            _repository.Create(n);
            return _mapper.Map<NoteDTO>(n);
        }

        public int DeleteNote(int id)
        {
            return _repository.Delete(id);
        }

        public NoteDTO GetNote(int id)
        {
            return _mapper.Map<NoteDTO>(_repository.Get(id));
        }

        public List<NoteDTO> GetNotes()
        {
            List<NoteDTO> res = [];
            foreach (Note n in _repository.GetAll())
            {
                res.Add(_mapper.Map<NoteDTO>(n));
            }
            return res;
        }

        public NoteDTO UpdateNote(NoteUpdateDTO item)
        {
            var n = _mapper.Map<Note>(item);
            _repository.Update(n);
            return _mapper.Map<NoteDTO>(n);
        }
    }
}
