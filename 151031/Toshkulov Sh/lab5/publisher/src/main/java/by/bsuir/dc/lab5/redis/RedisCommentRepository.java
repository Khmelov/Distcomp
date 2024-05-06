package by.bsuir.dc.lab5.redis;

import by.bsuir.dc.lab5.entities.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisCommentRepository {

        @Autowired
        private RedisTemplate template;
        private static final String HASH_KEY = "Comment";


        public Comment add(Comment comment){
            try {
                template.opsForHash().put(HASH_KEY, comment.getId(), comment);
                template.expire(HASH_KEY,10, TimeUnit.SECONDS);
                return comment;
            }catch (Exception e){
                return null;
            }
        }

        public Comment update(Comment comment){
            Optional<Comment> currentComment = getById(comment.getId());
            if(currentComment.isPresent()){
                template.opsForHash().put(HASH_KEY, comment.getId(), comment);

                template.expire(HASH_KEY,10, TimeUnit.SECONDS);
                return comment;
            }else{
                return null;
            }
        }

        public void delete(long id){
            template.opsForHash().delete(HASH_KEY,id);
        }

        public List<Comment> getAll(){
            List<Comment> comment = new ArrayList<>();
            List<Object> data = template.opsForHash().values(HASH_KEY);
            for(Object obj : data){
                comment.add((Comment) obj);
            }
            return comment;
        }

        public Optional<Comment> getById(long id){
            Object comment = template.opsForHash().get(HASH_KEY,id);
            return Optional.ofNullable((Comment)comment);
        }
}
