package work.wanghao.youthidere.model;

/**
 * Created by wangh on 2015-12-6-0006.
 */
public class Comments {
    private int id;
    private int user_id;
    private int post_id;
    private String content;
    private int floor;
    private String location;
    private String votes_count;
    private String created_at;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVotes_count() {
        return votes_count;
    }

    public void setVotes_count(String votes_count) {
        this.votes_count = votes_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", post_id=" + post_id +
                ", content='" + content + '\'' +
                ", floor=" + floor +
                ", location='" + location + '\'' +
                ", votes_count='" + votes_count + '\'' +
                ", created_at='" + created_at + '\'' +
                ", user=" + user +
                '}';
    }
}
