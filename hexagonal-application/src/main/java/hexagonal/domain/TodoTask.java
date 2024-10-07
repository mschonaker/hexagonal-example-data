package hexagonal.domain;

import java.sql.Timestamp;

public class TodoTask {

    private Long id = null;
    private String body = null;
    private Timestamp created = null;
    private Timestamp updated = null;

    public TodoTask() {
    }

    public TodoTask(Long id, String body, Timestamp created, Timestamp updated) {
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

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }
}
