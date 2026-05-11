package by.bsuir.task340.publisher.dto.response;

public class NoticeResponseTo {
    private Long id;
    private Long articleId;
    private String content;
    private String state;

    public NoticeResponseTo() {
    }

    public NoticeResponseTo(Long id, Long articleId, String content, String state) {
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}