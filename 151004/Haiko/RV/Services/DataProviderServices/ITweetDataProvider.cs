using RV.Views;
using RV.Views.DTO;

namespace RV.Services.DataProviderServices
{
    public interface ITweetDataProvider
    {
        TweetDTO CreateTweet(TweetAddDTO item);
        List<TweetDTO> GetTweets();
        TweetDTO GetTweet(int id);
        TweetDTO UpdateTweet(TweetUpdateDTO item);
        int DeleteTweet(int id);
    }
}
