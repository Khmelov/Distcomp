package org.ex.distributed_computing.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.ex.distributed_computing.dto.request.TweetRequestDTO;
import org.ex.distributed_computing.dto.response.TweetResponseDTO;
import org.ex.distributed_computing.exception.DuplicateDatabaseValueException;
import org.ex.distributed_computing.exception.NotFoundException;
import org.ex.distributed_computing.mapper.TweetMapper;
import org.ex.distributed_computing.model.Tweet;
import org.ex.distributed_computing.model.User;
import org.ex.distributed_computing.repository.TweetRepository;
import org.ex.distributed_computing.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TweetService {

  private final TweetRepository tweetRepository;
  private final UserRepository userRepository;
  private final TweetMapper tweetMapper;

  public List<TweetResponseDTO> getAllTweets() {
    return tweetMapper.toDtoList(tweetRepository.findAll());
  }

  public TweetResponseDTO getTweetById(Long id) {
    Tweet tweet = tweetRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Tweet not found"));
    return tweetMapper.toDto(tweet);
  }

  public TweetResponseDTO createTweet(TweetRequestDTO requestDTO) {
    User user = userRepository.findById(requestDTO.userId())
        .orElseThrow(() -> new NotFoundException("User not found"));

    if (tweetRepository.existsByTitle(requestDTO.title())) {
      throw new DuplicateDatabaseValueException();
    }

    Tweet tweet = new Tweet();
    tweet.setUser(user);
    tweet.setTitle(requestDTO.title());
    tweet.setContent(requestDTO.content());
    tweet.setCreated(LocalDateTime.now());
    tweet.setModified(LocalDateTime.now());

    tweetRepository.save(tweet);
    return tweetMapper.toDto(tweet);
  }

  public TweetResponseDTO updateTweet(TweetRequestDTO requestDTO) {
    Tweet tweet = tweetRepository.findById(requestDTO.id())
        .orElseThrow(() -> new NotFoundException("Tweet not found"));

    User user = userRepository.findById(requestDTO.userId())
        .orElseThrow(() -> new NotFoundException("User not found"));

    tweet.setUser(user);
    tweet.setTitle(requestDTO.title());
    tweet.setContent(requestDTO.content());
    tweet.setModified(LocalDateTime.now());

    tweetRepository.save(tweet);
    return tweetMapper.toDto(tweet);
  }

  public void deleteTweet(Long id) {
    Tweet tweet = tweetRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Tweet not found"));
    tweetRepository.delete(tweet);
  }
}

