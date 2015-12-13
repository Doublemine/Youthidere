package work.wanghao.youthidere.model;

/**
 * Created by wangh on 2015-12-6-0006.
 */
public class RecePostJsonData {
    private Post post;

    public Post getPost() {
        
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    @Override
    public String toString() {
        return "RecePostJsonData{" +
                "post=" + post +
                '}';
    }
}
