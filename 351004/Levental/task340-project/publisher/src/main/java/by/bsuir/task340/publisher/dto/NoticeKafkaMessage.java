package by.bsuir.task340.publisher.dto;

public class NoticeKafkaMessage {
    private Long id;
    private Long articleId;
    private String content;
    private NoticeState state;

    public NoticeKafkaMessage() {
    }

    public NoticeKafkaMessage(Long id, Long articleId, String content, NoticeState state) {
        this.id = id;
        this.articleId = articleId;
        this.content = content;
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public NoticeState getState() {
        return state;
    }

    public void setState(NoticeState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "NoticeKafkaMessage{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", content='" + content + '\'' +
                ", state=" + state +
                '}';
    }
}