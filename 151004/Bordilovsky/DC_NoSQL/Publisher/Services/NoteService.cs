using AutoMapper;
using DC_REST.DTOs.Request;
using DC_REST.DTOs.Response;
using DC_REST.Entities;
using DC_REST.Repositories;
using DC_REST.Services.Interfaces;
using DC_REST.Validators;
using System.Collections.Generic;

namespace DC_REST.Services
{
	public class NoteService : INoteService
	{
		private readonly IRepository<Note> _noteRepository;
		private readonly IMapper _mapper;
		private readonly IValidator<NoteRequestTo> _noteValidator;

		public NoteService(IRepository<Note> noteRepository, IMapper mapper, IValidator<NoteRequestTo> noteValidator)
		{
			_noteRepository = noteRepository;
			_mapper = mapper;
			_noteValidator = noteValidator;
		}

		public NoteResponseTo CreateNote(NoteRequestTo noteRequestDto)
		{
			if (!_noteValidator.Validate(noteRequestDto))
			{
				throw new ArgumentException("Invalid note data");
			}
			var note = _mapper.Map<Note>(noteRequestDto);
			//var currentId = _noteRepository.GetCurrentId();
			//note.Id = currentId;
			var createdNote = _noteRepository.Add(note);
			var responseDto = _mapper.Map<NoteResponseTo>(createdNote);

			return responseDto;
		}

		public NoteResponseTo GetNoteById(int id)
		{
			var note = _noteRepository.GetById(id);
			var noteDto = _mapper.Map<NoteResponseTo>(note);

			return noteDto;
		}

		public List<NoteResponseTo> GetAllNotes()
		{
			var notes = _noteRepository.GetAll();
			var noteDtos = _mapper.Map<List<NoteResponseTo>>(notes);

			return noteDtos;
		}

		public NoteResponseTo UpdateNote(int id, NoteRequestTo noteRequestDto)
		{
			if (!_noteValidator.Validate(noteRequestDto)) 
			{
				throw new ArgumentException("Invalid note data");
			}
			var existingNote = _noteRepository.GetById(id);
			if (existingNote == null)
			{
				
				return null;
			}

			_mapper.Map(noteRequestDto, existingNote);
			var updatedNote = _noteRepository.Update(id, existingNote);
			var responseDto = _mapper.Map<NoteResponseTo>(updatedNote);

			return responseDto;
		}

		public bool DeleteNote(int id)
		{
			var noteToDelete = _noteRepository.GetById(id);
			if (noteToDelete == null)
			{
				return false;
			}

			_noteRepository.Delete(id);
			return true;
		}
	}
}
