package work.wanghao.youthidere.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wangh on 2015-11-27-0027.
 */
public class PostItem extends RealmObject{
    
    @PrimaryKey
    private int id;
    private int user_id;
    private String category_slug;
    private String main_img;
    private String title;
    private String views_count;
    private int comments_count;
    private int wx_timeline;
    private int wx_message;
    private int qq_message;
    private int qzone;
    private String created_at;
    private String brief_content;
    private User user;
    private Category category;

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

    public String getCategory_slug() {
        return category_slug;
    }

    public void setCategory_slug(String category_slug) {
        this.category_slug = category_slug;
    }

    public String getMain_img() {
        return main_img;
    }

    public void setMain_img(String main_img) {
        this.main_img = main_img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViews_count() {
        return views_count;
    }

    public void setViews_count(String views_count) {
        this.views_count = views_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getWx_timeline() {
        return wx_timeline;
    }

    public void setWx_timeline(int wx_timeline) {
        this.wx_timeline = wx_timeline;
    }

    public int getWx_message() {
        return wx_message;
    }

    public void setWx_message(int wx_message) {
        this.wx_message = wx_message;
    }

    public int getQq_message() {
        return qq_message;
    }

    public void setQq_message(int qq_message) {
        this.qq_message = qq_message;
    }

    public int getQzone() {
        return qzone;
    }

    public void setQzone(int qzone) {
        this.qzone = qzone;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getBrief_content() {
        return brief_content;
    }

    public void setBrief_content(String brief_content) {
        this.brief_content = brief_content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

//    @Override
//    public String toString() {
//        return "PostItem{" +
//                "id=" + id +
//                ", user_id=" + user_id +
//                ", category_slug='" + category_slug + '\'' +
//                ", main_img='" + main_img + '\'' +
//                ", title='" + title + '\'' +
//                ", views_count='" + views_count + '\'' +
//                ", comments_count=" + comments_count +
//                ", wx_timeline=" + wx_timeline +
//                ", wx_message=" + wx_message +
//                ", qq_message=" + qq_message +
//                ", qzone=" + qzone +
//                ", created_at='" + created_at + '\'' +
//                ", brief_content='" + brief_content + '\'' +
//                ", user=" + user.toString() +
//                ", category=" + category.toString() +
//                '}';
//    }
//
//    @Override
//    public int compareTo(PostItem another) {
//        return another.getId()-this.getId();
//    }
}
