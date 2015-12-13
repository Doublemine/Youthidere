package work.wanghao.youthidere.model;

import work.wanghao.youthidere.model.AccountCommentParent;
import work.wanghao.youthidere.model.ReadHistoryPost;

/**
 * Created by wangh on 2015-12-12-0012.
 */
public class CompleteItemComments {
    private int id;
    private int user_id;
    private int post_id;
    private String content;
    private int floor;
    private String location;
    private int votes_count;
    private String created_at;
    private AccountCommentParent parent;
    private ReadHistoryPost post;

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

    public int getVotes_count() {
        return votes_count;
    }

    public void setVotes_count(int votes_count) {
        this.votes_count = votes_count;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public AccountCommentParent getParent() {
        return parent;
    }

    public void setParent(AccountCommentParent parent) {
        this.parent = parent;
    }

    public ReadHistoryPost getPost() {
        return post;
    }

    public void setPost(ReadHistoryPost post) {
        this.post = post;
    }
}
