package by.bsuir.dc.lab5.redis;

import by.bsuir.dc.lab5.entities.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisNewsRepository {

    @Autowired
    private RedisTemplate template;
    private static final String HASH_KEY = "News";


    public News add(News news){
        try {
            template.opsForHash().put(HASH_KEY, news.getId(), news);
            template.expire(HASH_KEY,10, TimeUnit.SECONDS);
            return news;
        }catch (Exception e){
            return null;
        }
    }

    public News update(News news){
        Optional<News> currentNews = getById(news.getId());
        if(currentNews.isPresent()){
            template.opsForHash().put(HASH_KEY, news.getId(), news);
            template.expire(HASH_KEY,10, TimeUnit.SECONDS);
            return news;
        }else{
            return null;
        }
    }

    public void delete(long id){
        template.opsForHash().delete(HASH_KEY,id);
    }

    public List<News> getAll(){
        List<News> news = new ArrayList<>();
        List<Object> data = template.opsForHash().values(HASH_KEY);
        for(Object obj : data){
            news.add((News) obj);
        }
        return news;
    }

    public Optional<News> getById(long id){
        Object news = template.opsForHash().get(HASH_KEY,id);
        return Optional.ofNullable((News)news);
    }

}
