package work.wanghao.youthidere.model;

/**
 * Created by wangh on 2015-12-12-0012.
 */
public class AccountFavorite {
    private int id;
    private int object_id;
    private int user_id;
    private String created_at;
    private ReadHistoryPost post;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getObject_id() {
        return object_id;
    }

    public void setObject_id(int object_id) {
        this.object_id = object_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public ReadHistoryPost getPost() {
        return post;
    }

    public void setPost(ReadHistoryPost post) {
        this.post = post;
    }
}
