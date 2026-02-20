using AutoMapper;
using Distcomp.Application.DTOs;
using Distcomp.Application.Exceptions;
using Distcomp.Application.Interfaces;
using Distcomp.Domain.Models;

namespace Distcomp.Application.Services
{
    public class NoteService : INoteService
    {
        private readonly IRepository<Note> _noteRepository;
        private readonly IRepository<Issue> _issueRepository;
        private readonly IMapper _mapper;

        public NoteService(IRepository<Note> noteRepository, IRepository<Issue> issueRepository, IMapper mapper)
        {
            _noteRepository = noteRepository;
            _issueRepository = issueRepository;
            _mapper = mapper;
        }

        public NoteResponseTo Create(NoteRequestTo request)
        {
            ValidateRequest(request);

            if (_issueRepository.GetById(request.IssueId) == null)
                throw new RestException(404, 40402, $"Issue with id {request.IssueId} not found. Cannot create note.");

            var note = _mapper.Map<Note>(request);
            var created = _noteRepository.Create(note);
            return _mapper.Map<NoteResponseTo>(created);
        }

        public NoteResponseTo? GetById(long id)
        {
            var note = _noteRepository.GetById(id);
            if (note == null) throw new RestException(404, 40404, "Note not found");
            return _mapper.Map<NoteResponseTo>(note);
        }

        public IEnumerable<NoteResponseTo> GetAll() =>
            _mapper.Map<IEnumerable<NoteResponseTo>>(_noteRepository.GetAll());

        public NoteResponseTo Update(long id, NoteRequestTo request)
        {
            var existing = _noteRepository.GetById(id);
            if (existing == null) throw new RestException(404, 40404, "Cannot update: Note not found");

            ValidateRequest(request);

            if (_issueRepository.GetById(request.IssueId) == null)
                throw new RestException(404, 40402, "Cannot update note: Linked Issue not found");

            _mapper.Map(request, existing);
            existing.Id = id;
            _noteRepository.Update(existing);

            return _mapper.Map<NoteResponseTo>(existing);
        }

        public bool Delete(long id)
        {
            if (_noteRepository.GetById(id) == null) throw new RestException(404, 40404, "Cannot delete: Note not found");
            return _noteRepository.Delete(id);
        }

        private void ValidateRequest(NoteRequestTo request)
        {
            if (request.Content.Length < 2 || request.Content.Length > 2048)
                throw new RestException(400, 40008, "Note content must be between 2 and 2048 characters");
        }
    }
}