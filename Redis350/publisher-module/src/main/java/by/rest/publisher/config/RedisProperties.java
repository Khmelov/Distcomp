package by.rest.publisher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "redis")
public class RedisProperties {
    
    private Ttl ttl = new Ttl();
    
    public static class Ttl {
        private int hours = 24;
        private int editors = 12;
        private int stories = 6;
        private int tags = 24;
        private int comments = 3;
        
        // Геттеры и сеттеры
        public int getHours() { return hours; }
        public void setHours(int hours) { this.hours = hours; }
        
        public int getEditors() { return editors; }
        public void setEditors(int editors) { this.editors = editors; }
        
        public int getStories() { return stories; }
        public void setStories(int stories) { this.stories = stories; }
        
        public int getTags() { return tags; }
        public void setTags(int tags) { this.tags = tags; }
        
        public int getComments() { return comments; }
        public void setComments(int comments) { this.comments = comments; }
    }
    
    public Ttl getTtl() { return ttl; }
    public void setTtl(Ttl ttl) { this.ttl = ttl; }
}