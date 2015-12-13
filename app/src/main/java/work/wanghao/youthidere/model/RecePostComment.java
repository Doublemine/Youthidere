package work.wanghao.youthidere.model;

/**
 * Created by wangh on 2015-12-8-0008.
 */
public class RecePostComment {
    private ResponseComment comment;

    public ResponseComment getComment() {
        return comment;
    }

    public void setComment(ResponseComment comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "RecePostComment{" +
                "comment=" + comment +
                '}';
    }
}
