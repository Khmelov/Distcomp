namespace RV.Services.DataProviderServices
{
    public class DataProvider : IDataProvider
    {
        public IUserDataProvider userDataProvider { get; }

        public ITweetDataProvider newsDataProvider { get; }

        public INoteDataProvider noteDataProvider { get; }

        public IStickerDataProvider stickerDataProvider { get; }

        public DataProvider(
            IUserDataProvider userDataProvider,
            ITweetDataProvider newsDataProvider,
            INoteDataProvider noteDataProvider,
            IStickerDataProvider stickerDataProvider
            )
        {
            this.userDataProvider = userDataProvider;
            this.newsDataProvider = newsDataProvider;
            this.noteDataProvider = noteDataProvider;
            this.stickerDataProvider = stickerDataProvider;
        }
    }
}
