using AutoMapper;
using rest1.application.DTOs.requests;
using rest1.application.DTOs.responses;
using rest1.application.exceptions;
using rest1.application.interfaces;
using rest1.application.interfaces.services;
using rest1.core.entities;

namespace rest1.application.services;

public class NoteService : INoteService
    {
        private readonly IMapper _mapper;
        private readonly INoteRepository _noteRepository;

        public NoteService(IMapper mapper, INoteRepository repository)
        {
            _mapper = mapper;
            _noteRepository = repository;
        }

        public async Task<NoteResponseTo> CreateNote(NoteRequestTo createNoteRequestTo)
        {
            Note noteFromDto = _mapper.Map<Note>(createNoteRequestTo);

            Note createdNote = await _noteRepository.AddAsync(noteFromDto);

            NoteResponseTo dtoFromCreatedNote =
                _mapper.Map<NoteResponseTo>(createdNote);

            return dtoFromCreatedNote;
        }

        public async Task DeleteNote(NoteRequestTo deleteNoteRequestTo)
        {
            Note noteFromDto = _mapper.Map<Note>(deleteNoteRequestTo);

            _ = await _noteRepository.DeleteAsync(noteFromDto)
                ?? throw new NoteNotFoundException(
                    $"Delete note {noteFromDto} was not found");
        }

        public async Task<IEnumerable<NoteResponseTo>> GetAllNotes()
        {
            IEnumerable<Note> allNotes =
                await _noteRepository.GetAllAsync();

            var response = new List<NoteResponseTo>();

            foreach (Note note in allNotes)
            {
                response.Add(_mapper.Map<NoteResponseTo>(note));
            }

            return response;
        }

        public async Task<NoteResponseTo> GetNote(NoteRequestTo getNoteRequestTo)
        {
            Note noteFromDto = _mapper.Map<Note>(getNoteRequestTo);

            Note demandedNote =
                await _noteRepository.GetByIdAsync(noteFromDto.Id)
                ?? throw new NoteNotFoundException(
                    $"Demanded note {noteFromDto} was not found");

            return _mapper.Map<NoteResponseTo>(demandedNote);
        }

        public async Task<NoteResponseTo> UpdateNote(NoteRequestTo updateNoteRequestTo)
        {
            Note noteFromDto = _mapper.Map<Note>(updateNoteRequestTo);

            Note updatedNote =
                await _noteRepository.UpdateAsync(noteFromDto)
                ?? throw new NoteNotFoundException(
                    $"Update note {noteFromDto} was not found");

            return _mapper.Map<NoteResponseTo>(updatedNote);
        }
    }