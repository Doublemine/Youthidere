package work.wanghao.youthidere.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by wangh on 2015-11-27-0027.
 */
public class Category extends RealmObject{
    @PrimaryKey
    private int id;
    private String icon;
    private String name;
    private String slug;
    private String description;
    private int post_counts;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPost_counts() {
        return post_counts;
    }

    public void setPost_counts(int post_counts) {
        this.post_counts = post_counts;
    }

//    @Override
//    public String toString() {
//        return "Category{" +
//                "id=" + id +
//                ", icon='" + icon + '\'' +
//                ", name='" + name + '\'' +
//                ", slug='" + slug + '\'' +
//                ", description='" + description + '\'' +
//                ", post_counts=" + post_counts +
//                '}';
//    }
}
