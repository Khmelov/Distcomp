using AutoMapper;
using DiscussionModule.DTOs.requests;
using DiscussionModule.DTOs.responses;
using DiscussionModule.interfaces;
using DiscussionModule.models;

namespace DiscussionModule.services;

public class NoteService : INoteService
{
    private readonly IMapper _mapper;
    private readonly INoteRepository _repository;

    public NoteService(IMapper mapper, INoteRepository repository)
    {
        _mapper = mapper;
        _repository = repository;
    }

    public async Task<NoteResponseTo> CreateNote(NoteRequestTo createNoteRequestTo)
    {
        Note noteFromDto = _mapper.Map<Note>(createNoteRequestTo);
        
        Note createdNote = await _repository.AddAsync(noteFromDto);

        NoteResponseTo dtoFromCreatedNote = _mapper.Map<NoteResponseTo>(createdNote);

        return dtoFromCreatedNote;
    }

    public async Task<IEnumerable<NoteResponseTo>> GetAllNotes()
    {
        IEnumerable<Note> allNotes = await _repository.GetAllAsync();

        var allNotesResponseTos = new List<NoteResponseTo>();

        foreach (Note note in allNotes)
        {
            NoteResponseTo noteTo = _mapper.Map<NoteResponseTo>(note);
            allNotesResponseTos.Add(noteTo);
        }

        return allNotesResponseTos;
    }

    public async Task<NoteResponseTo?> GetNote(NoteRequestTo getNoteRequestTo)
    {
        Note noteFromDto = _mapper.Map<Note>(getNoteRequestTo);

        Note demandedNote = await _repository.GetByIdAsync(noteFromDto.Id)
            ?? throw new ArgumentException($"Get note {noteFromDto} was not found");

        NoteResponseTo demandedNoteResponseTo = _mapper.Map<NoteResponseTo>(demandedNote);

        return demandedNoteResponseTo;
    }

    public async Task<NoteResponseTo?> UpdateNote(NoteRequestTo updateNoteRequestTo)
    {
        Note noteFromDto = _mapper.Map<Note>(updateNoteRequestTo);

        Note updatedNote = await _repository.UpdateAsync(noteFromDto)
            ?? throw new ArgumentException($"Update note {noteFromDto} was not found");

        NoteResponseTo updatedNoteResponseTo = _mapper.Map<NoteResponseTo>(updatedNote);

        return updatedNoteResponseTo;
    }

    public async Task DeleteNote(NoteRequestTo deleteNoteRequestTo)
    {
        Note noteFromDto = _mapper.Map<Note>(deleteNoteRequestTo);

        await _repository.DeleteAsync(noteFromDto);
    }
}