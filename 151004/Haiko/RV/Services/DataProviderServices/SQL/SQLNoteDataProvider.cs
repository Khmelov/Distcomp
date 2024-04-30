using AutoMapper;
using RV.Models;
using RV.Repositories;
using RV.Views.DTO;

namespace RV.Services.DataProviderServices.SQL
{
    public class SQLNoteDataProvider : INoteDataProvider
    {
        private IRepository<Note> _repository;
        private IMapper _mapper;

        public SQLNoteDataProvider(IRepository<Note> repository, IMapper mapper)
        {
            _repository = repository;
            _mapper = mapper;
        }
        public NoteDTO CreateNote(NoteAddDTO item)
        {
            Note n = _mapper.Map<Note>(item);
            var res = _repository.Create(n);
            return _mapper.Map<NoteDTO>(res);
        }

        public int DeleteNote(int id)
        {
            int res = _repository.Delete(id);
            return res;
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
            var res = _repository.Update(n);
            return _mapper.Map<NoteDTO>(res);
        }
    }
}
