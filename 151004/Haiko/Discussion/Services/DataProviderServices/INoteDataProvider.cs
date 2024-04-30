using Discussion.Views.DTO;

namespace Discussion.Services.DataProviderServices
{
    public interface INoteDataProvider
    {
        NoteDTO CreateNote(NoteAddDTO item);
        List<NoteDTO> GetNotes();
        NoteDTO GetNote(int id);
        NoteDTO UpdateNote(NoteUpdateDTO item);
        int DeleteNote(int id);
    }
}
