package by.egorsosnovski.distcomp.services;

import by.egorsosnovski.distcomp.dto.*;
import by.egorsosnovski.distcomp.entities.Sticker;
import by.egorsosnovski.distcomp.entities.Tweet;
import by.egorsosnovski.distcomp.repositories.StickerRepository;
import by.egorsosnovski.distcomp.repositories.TweetRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TweetService {
    public final TweetRepository repImpl;
    private final StickerRepository stickerRepository;
    @Qualifier("tweetMapper")
    public final TweetMapper mapper;

    public List<TweetResponseTo> getAll() {
        return repImpl.getAll().map(mapper::out).toList();
    }
    @Cacheable(value = "tweets", key = "#id")
    public TweetResponseTo getById(Long id) {
        return repImpl.get(id).map(mapper::out).orElseThrow();
    }
    @CachePut(value = "tweets", key = "#req.id")
    public TweetResponseTo create(TweetRequestTo req) {

        return repImpl.create(map(req)).map(mapper::out).orElseThrow();
    }
    private Tweet map(TweetRequestTo req) {
        Tweet tweet = mapper.in(req);
        Set<Sticker> stickers = (req.getStickers() == null || req.getStickers().isEmpty())
                ? Collections.emptySet()
                : req.getStickers().stream()
                .map(name -> stickerRepository.findByName(name).orElseGet(() -> {
                    Sticker newSticker = new Sticker();
                    newSticker.setName(name);
                    return stickerRepository.save(newSticker);
                }))
                .collect(Collectors.toSet());

        tweet.setStickers(stickers);
        return tweet;
    }
    @CachePut(value = "tweets", key = "#req.id")
    public TweetResponseTo update(TweetRequestTo req) {
        return repImpl.update(map(req)).map(mapper::out).orElseThrow();
    }
    @CacheEvict(value = "tweets", key = "#id")
    @Transactional
    public void delete(Long id) {
        Tweet tweet = repImpl.findById(id)
                .orElseThrow(() -> new NoSuchElementException("No element with id " + id));
        Set<Sticker> stickers = tweet.getStickers();
        repImpl.delete(id);

        for (Sticker sticker : stickers) {
            if (repImpl.countTweetsWithSticker(sticker.getId()) == 0) {
                stickerRepository.delete(sticker);
            }
        }

    }
}

