package com.example.app.repository;

// ... существующие импорты ...

@Repository
public interface ReactionRepository extends CassandraRepository<Reaction, ReactionKey> {
    
    // Существующие методы
    @Query("SELECT * FROM tbl_reaction WHERE tweet_id = ?0")
    List<Reaction> findByTweetId(Long tweetId);
    
    List<Reaction> findByKeyCountryAndKeyTweetId(String country, Long tweetId);
    
    Optional<Reaction> findByKeyCountryAndKeyTweetIdAndKeyId(String country, Long tweetId, Long id);
    
    @Query("DELETE FROM tbl_reaction WHERE tweet_id = ?0")
    void deleteByTweetId(Long tweetId);
    
    void deleteByKeyCountryAndKeyTweetId(String country, Long tweetId);
    
    boolean existsByKeyCountryAndKeyTweetIdAndKeyId(String country, Long tweetId, Long id);
    
    // НОВЫЙ МЕТОД: Найти по content (если нужно)
    @Query("SELECT * FROM tbl_reaction WHERE content CONTAINS ?0 ALLOW FILTERING")
    List<Reaction> findByContentContaining(String content);
}