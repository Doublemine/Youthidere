package work.wanghao.youthidere.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wangh on 2015-12-2-0002.
 */
public class Explore extends RealmObject{
    @PrimaryKey
    private int id;
    private String user_id;
    private String pic_url;
    private String pic_thumbnail_url;
    private String pic_small_url;
    private String text;
    private String created_at;
    private String location;
    private String likes_count;
    private User user;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    public String getPic_thumbnail_url() {
        return pic_thumbnail_url;
    }

    public void setPic_thumbnail_url(String pic_thumbnail_url) {
        this.pic_thumbnail_url = pic_thumbnail_url;
    }

    public String getPic_small_url() {
        return pic_small_url;
    }

    public void setPic_small_url(String pic_small_url) {
        this.pic_small_url = pic_small_url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(String likes_count) {
        this.likes_count = likes_count;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
