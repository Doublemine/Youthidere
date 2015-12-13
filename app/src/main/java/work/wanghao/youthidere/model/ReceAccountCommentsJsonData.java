package work.wanghao.youthidere.model;

import java.util.List;

/**
 * Created by wangh on 2015-12-12-0012.
 */
public class ReceAccountCommentsJsonData {
    private User user;
    private List<CompleteItemComments> comments;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<CompleteItemComments> getComments() {
        return comments;
    }

    public void setComments(List<CompleteItemComments> comments) {
        this.comments = comments;
    }
}
