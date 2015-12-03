package work.wanghao.youthidere.model;

/**
 * Created by wangh on 2015-11-27-0027.
 */
public class Token {
    
    private String token;
    private User user;
    private String error;
    private String message;//注册过于频繁会遇到
    private int status_code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        if(error!=null){
            return "Token{" +
                    "token='" + token + '\'' +
                    ", error=" + error +
                    '}';
        }
        return "Token{" +
                "token='" + token + '\'' +
                ", user=" + user.toString() +
                '}';
    }
}
