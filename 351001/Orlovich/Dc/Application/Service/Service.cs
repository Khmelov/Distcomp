using Application.Repository;
using AutoMapper;
using Domain.Models;

namespace Application.Service;

public class Service : IService
{
    private readonly IRepository<Editor> _editorRepository;
    private readonly IMapper _mapper;
    
    public Service(IRepository<Editor> repository, IMapper mapper)
    {
        _editorRepository =  repository;
        _mapper = mapper;
    }

    public async Task<IList<EditorResponseTo>> GetAllEditorsAsync()
    {
        var list = await _editorRepository.GetAllAsync();
        
        return list.Select(x => _mapper.Map<EditorResponseTo>(x)).ToList();
    }

    public async Task<EditorResponseTo> GetEditorByIdAsync(long id)
    {
        return _mapper.Map<EditorResponseTo>(await _editorRepository.GetByIdAsync(id));
    }

    public async Task<EditorResponseTo> AddEditorAsync(EditorRequestTo editor)
    { 
        var a = _mapper.Map<Editor>(editor);
        a.id = new Random().NextInt64();
        
        await _editorRepository.AddAsync(a);
        return _mapper.Map<EditorResponseTo>(a);
    }

    public async Task<EditorResponseTo?> UpdateEditorAsync(long id, EditorRequestTo editor)
    {
        var a = _mapper.Map<Editor>(editor);
        a = await _editorRepository.UpdateAsync(id,a);
        
        if(a == null)
            return null;
        
        return _mapper.Map<EditorResponseTo>(a);
    }

    public async Task<int> DeleteEditorAsync(long id)
    {
        return await _editorRepository.DeleteAsync(id);
    }
}