using Application.DTOs.Requests;
using Application.DTOs.Responses;
using Application.Exceptions;
using Application.Interfaces;
using AutoMapper;
using Core.Entities;

namespace Application.Services
{
    public class EditorService : IEditorService
    {
        private readonly IMapper _mapper;

        private readonly IEditorRepository _editorRepository;

        public EditorService(IMapper mapper, IEditorRepository repository)
        {
            _mapper = mapper;
            _editorRepository = repository;
        }

        public async Task<EditorResponseTo> CreateEditor(EditorRequestTo createEditorRequestTo)
        {
            Editor editorFromDto = _mapper.Map<Editor>(createEditorRequestTo);

            Editor createdEditor = await _editorRepository.AddAsync(editorFromDto);

            EditorResponseTo dtoFromCreatedEditor = _mapper.Map<EditorResponseTo>(createdEditor);

            return dtoFromCreatedEditor;
        }

        public async Task DeleteEditor(EditorRequestTo deleteEditorRequestTo)
        {
            Editor editorFromDto = _mapper.Map<Editor>(deleteEditorRequestTo);

            _ = await _editorRepository.DeleteAsync(editorFromDto) 
                ?? throw new EditorNotFoundException($"Delete editor {editorFromDto} was not found");
        }

        public async Task<IEnumerable<EditorResponseTo>> GetAllEditors()
        {
            IEnumerable<Editor> allEditors = await _editorRepository.GetAllAsync();

            var allEditorsResponseTos = new List<EditorResponseTo>();
            foreach (Editor editor in allEditors)
            {
                EditorResponseTo editorTo = _mapper.Map<EditorResponseTo>(editor);
                allEditorsResponseTos.Add(editorTo);
            }

            return allEditorsResponseTos;
        }

        public async Task<EditorResponseTo> GetEditor(EditorRequestTo getEditorsRequestTo)
        {
            Editor editorFromDto = _mapper.Map<Editor>(getEditorsRequestTo);

            Editor demandedEditor = await _editorRepository.GetByIdAsync(editorFromDto.Id) 
                ?? throw new EditorNotFoundException($"Demanded editor {editorFromDto} was not found");

            EditorResponseTo demandedEditorResponseTo = _mapper.Map<EditorResponseTo>(demandedEditor);

            return demandedEditorResponseTo;
        }

        public async Task<EditorResponseTo> UpdateEditor(EditorRequestTo updateEditorRequestTo)
        {
            Editor editorFromDto = _mapper.Map<Editor>(updateEditorRequestTo);

            Editor updateEditor = await _editorRepository.UpdateAsync(editorFromDto) 
                ?? throw new EditorNotFoundException($"Update editor {editorFromDto} was not found");

            EditorResponseTo updateEditorResponseTo = _mapper.Map<EditorResponseTo>(updateEditor);

            return updateEditorResponseTo;
        }
    }
}
