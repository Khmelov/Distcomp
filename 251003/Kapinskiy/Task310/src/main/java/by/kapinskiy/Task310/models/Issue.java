package by.kapinskiy.Task310.models;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Issue {
    private long id;
    private User user;
    private String title;
    private String content;
    private Date created;
    private Date modified;

    private List<Tag> tags = new ArrayList<>();
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return id == issue.id && user.equals(issue.user) && title.equals(issue.title) && content.equals(issue.content) && created.equals(issue.created) && modified.equals(issue.modified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, title, content, created, modified);
    }
}
