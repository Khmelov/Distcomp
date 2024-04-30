namespace Discussion.Services.DataProviderServices
{
    public class DataProvider : IDataProvider
    {
        public INoteDataProvider noteDataProvider { get; }

        public DataProvider(
            INoteDataProvider noteDataProvider
            )
        {
            this.noteDataProvider = noteDataProvider;
        }
    }
}
