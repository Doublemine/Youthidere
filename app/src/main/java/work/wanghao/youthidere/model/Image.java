package work.wanghao.youthidere.model;

/**
 * Created by wangh on 2015-12-12-0012.
 */
public class Image {
    
    private String user_id;
    private String pic_url;
    private String pic_thumbnail_url;
    private String pic_small_url;
    private String text;
    private String location;
    private String created_at;
    private int id;


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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
