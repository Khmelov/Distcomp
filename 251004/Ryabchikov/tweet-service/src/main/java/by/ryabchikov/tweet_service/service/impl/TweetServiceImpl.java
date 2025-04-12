package by.ryabchikov.tweet_service.service.impl;

import by.ryabchikov.tweet_service.dto.tweet.TweetRequestTo;
import by.ryabchikov.tweet_service.dto.tweet.TweetResponseTo;
import by.ryabchikov.tweet_service.dto.tweet.TweetUpdateRequestTo;
import by.ryabchikov.tweet_service.entity.Creator;
import by.ryabchikov.tweet_service.entity.Tweet;
import by.ryabchikov.tweet_service.exception.CreatorNotFoundException;
import by.ryabchikov.tweet_service.exception.TweetNotFoundException;
import by.ryabchikov.tweet_service.exception.TweetTitleDuplicationException;
import by.ryabchikov.tweet_service.mapper.TweetMapper;
import by.ryabchikov.tweet_service.repository.CreatorRepository;
import by.ryabchikov.tweet_service.repository.TweetRepository;
import by.ryabchikov.tweet_service.service.TweetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {
    private final TweetRepository tweetRepository;
    private final CreatorRepository creatorRepository;
    private final TweetMapper tweetMapper;

    private void checkOnTitleDuplication(String title) {
        Optional<Tweet> optionalTweet = tweetRepository.findByTitle(title);
        if (optionalTweet.isPresent()) {
            throw TweetTitleDuplicationException.byTitle(title);
        }
    }

    @Override
    @Transactional
    public TweetResponseTo create(TweetRequestTo tweetRequestTo) {
        checkOnTitleDuplication(tweetRequestTo.title());

        Tweet tweet = tweetMapper.toTweet(tweetRequestTo);

        if (tweet.getMarks() != null) {
            tweet.getMarks().forEach(mark -> {
                if (mark.getTweets() == null) {
                    mark.setTweets(new ArrayList<>());
                }
                mark.getTweets().add(tweet);
            });
        }

        return tweetMapper.toTweetResponseTo(
                tweetRepository.save(tweet)
        );
    }

    @Override
    @Transactional
    public List<TweetResponseTo> readAll() {
        return tweetRepository.findAll()
                .stream()
                .map(tweetMapper::toTweetResponseTo)
                .toList();
    }

    @Override
    @Transactional
    public TweetResponseTo readById(Long id) {
        return tweetMapper.toTweetResponseTo(
                tweetRepository.findById(id).orElseThrow(() -> TweetNotFoundException.byId(id))
        );
    }

    @Override
    @Transactional
    public TweetResponseTo update(TweetUpdateRequestTo tweetUpdateRequestTo) {
        long tweetId = tweetUpdateRequestTo.id();
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> TweetNotFoundException.byId(tweetId));

        long creatorId = tweetUpdateRequestTo.creatorId();
        Creator creator = creatorRepository.findById(creatorId)
                .orElseThrow(() -> CreatorNotFoundException.byId(creatorId));

        tweet.setTitle(tweetUpdateRequestTo.title());
        tweet.setContent(tweetUpdateRequestTo.content());
        tweet.setCreator(creator);

        return tweetMapper.toTweetResponseTo(tweet);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        tweetRepository.findById(id)
                .orElseThrow(() -> TweetNotFoundException.byId(id));
        tweetRepository.deleteById(id);
    }
}
