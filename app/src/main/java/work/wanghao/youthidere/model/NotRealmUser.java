package work.wanghao.youthidere.model;

/**
 * Created by wangh on 2015-11-27-0027.
 */
public class NotRealmUser {
    private int id;
    private String uuid;
    private String name;
    private String email;
    private int sex;
    private int score;
    private String avatar_url;
    private String created_at;
    private int post_read_count;
    private int comments_count;
    private int favo_count;
    private boolean weixin;
    private boolean qq;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getPost_read_count() {
        return post_read_count;
    }

    public void setPost_read_count(int post_read_count) {
        this.post_read_count = post_read_count;
    }

    public int getComments_count() {
        return comments_count;
    }

    public void setComments_count(int comments_count) {
        this.comments_count = comments_count;
    }

    public int getFavo_count() {
        return favo_count;
    }

    public void setFavo_count(int favo_count) {
        this.favo_count = favo_count;
    }

    public boolean isWeixin() {
        return weixin;
    }

    public void setWeixin(boolean weixin) {
        this.weixin = weixin;
    }

    public boolean isQq() {
        return qq;
    }

    public void setQq(boolean qq) {
        this.qq = qq;
    }

//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", uuid='" + uuid + '\'' +
//                ", name='" + name + '\'' +
//                ", email='" + email + '\'' +
//                ", sex=" + sex +
//                ", score=" + score +
//                ", avatar_url='" + avatar_url + '\'' +
//                ", created_at='" + created_at + '\'' +
//                ", post_read_count=" + post_read_count +
//                ", comments_count=" + comments_count +
//                ", favo_count=" + favo_count +
//                ", weixin=" + weixin +
//                ", qq=" + qq +
//                '}';
//    }
}
