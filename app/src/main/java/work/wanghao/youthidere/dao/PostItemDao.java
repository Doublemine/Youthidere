package work.wanghao.youthidere.dao;

import java.util.List;

import work.wanghao.youthidere.model.PostItem;

/**
 * Created by wangh on 2015-12-1-0001.
 */
public interface PostItemDao {
    
    List<PostItem> selectPostItem(int id,boolean isTrue);
}
