using RV.Views;
using RV.Views.DTO;

namespace RV.Services.DataProviderServices
{
    public interface IDataProvider : IUserDataProvider, ITweetDataProvider, INoteDataProvider, IStickerDataProvider
    {
        public IUserDataProvider userDataProvider { get; }
        public ITweetDataProvider newsDataProvider { get; }
        public INoteDataProvider noteDataProvider { get; }
        public IStickerDataProvider stickerDataProvider { get; }

        UserDTO IUserDataProvider.CreateUser(UserAddDTO item)
        {
            return userDataProvider.CreateUser(item);
        }

        List<UserDTO> IUserDataProvider.GetUsers()
        {
            return userDataProvider.GetUsers();
        }

        UserDTO IUserDataProvider.GetUser(int id)
        {
            return userDataProvider.GetUser(id);
        }

        UserDTO IUserDataProvider.UpdateUser(UserUpdateDTO item)
        {
            return userDataProvider.UpdateUser(item);
        }

        int IUserDataProvider.DeleteUser(int id)
        {
            return userDataProvider.DeleteUser(id);
        }

        TweetDTO ITweetDataProvider.CreateTweet(TweetAddDTO item)
        {
            return newsDataProvider.CreateTweet(item);
        }

        List<TweetDTO> ITweetDataProvider.GetTweets()
        {
            return newsDataProvider.GetTweets();
        }

        TweetDTO ITweetDataProvider.GetTweet(int id)
        {
            return newsDataProvider.GetTweet(id);
        }

        TweetDTO ITweetDataProvider.UpdateTweet(TweetUpdateDTO item)
        {
            return newsDataProvider.UpdateTweet(item);
        }

        int ITweetDataProvider.DeleteTweet(int id)
        {
            return newsDataProvider.DeleteTweet(id);
        }

        NoteDTO INoteDataProvider.CreateNote(NoteAddDTO item)
        {
            var res = noteDataProvider.CreateNote(item);
            return res;
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

        StickerDTO IStickerDataProvider.CreateSticker(StickerAddDTO item)
        {
            return stickerDataProvider.CreateSticker(item);
        }

        List<StickerDTO> IStickerDataProvider.GetStickers()
        {
            return stickerDataProvider.GetStickers();
        }

        StickerDTO IStickerDataProvider.GetSticker(int id)
        {
            return stickerDataProvider.GetSticker(id);
        }

        StickerDTO IStickerDataProvider.UpdateSticker(StickerUpdateDTO item)
        {
            return stickerDataProvider.UpdateSticker(item);
        }

        int IStickerDataProvider.DeleteSticker(int id)
        {
            return stickerDataProvider.DeleteSticker(id);
        }
    }
}
