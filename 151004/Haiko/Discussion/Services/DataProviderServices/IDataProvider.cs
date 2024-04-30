using Discussion.Views.DTO;

namespace Discussion.Services.DataProviderServices
{
    public interface IDataProvider : INoteDataProvider
    {
        public INoteDataProvider noteDataProvider { get; }

        NoteDTO INoteDataProvider.CreateNote(NoteAddDTO item)
        {
            return noteDataProvider.CreateNote(item);
        }

        List<NoteDTO> INoteDataProvider.GetNotes()
        {
            return noteDataProvider.GetNotes();
        }

        NoteDTO INoteDataProvider.GetNote(int id)
        {
            return noteDataProvider.GetNote(id);
        }

        NoteDTO INoteDataProvider.UpdateNote(NoteUpdateDTO item)
        {
            return noteDataProvider.UpdateNote(item);
        }

        int INoteDataProvider.DeleteNote(int id)
        {
            return noteDataProvider.DeleteNote(id);
        }
    }
}
