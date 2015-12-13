package work.wanghao.youthidere.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangh on 2015-12-6-0006.
 */
public class ImgItem {
    private String title;
    private List<String> imgUrl=new ArrayList<String>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl.add(imgUrl);
    }

    @Override
    public String toString() {
        return "ImgItem{" +
                "title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
