package hexagonal.domain;

public class TodoTask {

    private Long id = null;
    private String body = null;
    private long created = 0L;
    private long updated = 0L;

    public TodoTask() {
    }

    public TodoTask(Long id, String body, long created, long updated) {
        this.id = id;
        this.body = body;
        this.created = created;
        this.updated = updated;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }
}
