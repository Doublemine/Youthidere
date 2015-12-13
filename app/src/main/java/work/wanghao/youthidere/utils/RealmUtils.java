package work.wanghao.youthidere.utils;

import android.content.Context;
import android.util.Log;

import com.wangh.http.okhttp.request.OkHttpRequest;

import java.io.IOException;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.exceptions.RealmError;
import io.realm.exceptions.RealmException;
import work.wanghao.youthidere.config.GlobalConfig;
import work.wanghao.youthidere.db.ExploreItemRealmHelper;
import work.wanghao.youthidere.db.PostItemRealmHelper;
import work.wanghao.youthidere.db.TabCategoriesHelper;
import work.wanghao.youthidere.model.Category;
import work.wanghao.youthidere.model.Explore;
import work.wanghao.youthidere.model.PostItem;
import work.wanghao.youthidere.model.RecItemJsonData;
import work.wanghao.youthidere.model.ReceCategoryJsonData;
import work.wanghao.youthidere.model.ReceExploreJsonData;

/**
 * Created by wangh on 2015-12-3-0003.
 */
public class RealmUtils {

    private static final int DATA_LEGTH = 20;
    private static final String BROWSER_UA = "Mozilla/5.0 (Linux U 10.0; Android 5.0;zh-CN; Doublemine Build/2015) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
    private static final String CATEGORY_ADDR = "http://www.qingniantuzhai.com/api/categories";

    /**
     * 通过数据库查找返回分类查看数据，没有将从网络获取数据
     *
     * @param currentId
     * @param category
     * @param context
     * @return
     */
    public static List<PostItem> getOldPostItemByCategoryFromRealm(int currentId, String category, Context context) {
        int afterId = currentId - DATA_LEGTH;
        if (afterId <= 0) {
            afterId = 0;
        }

        /**
         * 先查找数据
         */
        Realm realm = PostItemRealmHelper.getRealm(context);
        List<PostItem> queryItems = realm.where(PostItem.class)
                .equalTo("category_slug", category)
                .lessThan("id", currentId)
                .greaterThanOrEqualTo("id", afterId)
                .findAllSorted("id", Sort.DESCENDING);

        if (queryItems.isEmpty() || queryItems.size() < DATA_LEGTH) {//数据库中的数据量少于请求量--》请求网络
            //获取数据并写入数据库
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(HttpUtils.getOldPostItemByCategoryFromServer(currentId, category));
            realm.commitTransaction();
            //再次查询
            List<PostItem> queryItemsAgain = realm.where(PostItem.class)
                    .equalTo("category_slug", category)
                    .lessThan("id", currentId)
                    .greaterThanOrEqualTo("id", afterId)
                    .findAllSorted("id", Sort.DESCENDING);
            return queryItemsAgain;
        } else {
            return queryItems;
        }
    }


    /**
     * 通过数据库查找返回分类查看数据，没有将从网络获取数据
     *
     * @param currentId
     * @param category
     * @param context
     * @return
     */

    public static List<PostItem> getNewPostItemByCategoryFromRealm(int currentId, String category, Context context) {


        /**
         * 直接从网络获取数据写入数据库
         */
        Realm realm = PostItemRealmHelper.getRealm(context);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(HttpUtils.getNewPostItemByCategoryFromServer(currentId, category));
        realm.commitTransaction();

        /**
         * 查询数据库并返回数据
         */
        List<PostItem> queryItems = realm.where(PostItem.class)
                .equalTo("category_slug", category)
                .greaterThan("id", currentId)
                .lessThanOrEqualTo("id", currentId + DATA_LEGTH)
                .findAllSorted("id", Sort.DESCENDING);
        if (queryItems.isEmpty()) {
            return null;
        } else {
            return queryItems;
        }
    }


    /**
     * 通过数据库查找返回图摘查看数据，没有将从网络获取数据
     *
     * @param currentId
     * @return
     */
    public static RealmResults<PostItem> getNewImgDataFromRealm(int currentId, Context context) {
        /**
         * 直接从网络获取数据写入数据库
         */
        Realm realm = PostItemRealmHelper.getRealm(context);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(HttpUtils.getNewImgDataFromServer(currentId));
        realm.commitTransaction();

        /**
         * 查询数据库并返回数据
         */
        RealmResults<PostItem> queryItems = realm.where(PostItem.class)
                .greaterThan("id", currentId)
                .lessThanOrEqualTo("id", currentId + DATA_LEGTH)
                .findAllSorted("id", Sort.DESCENDING);


        Log.e("Realm DEBUG", String.valueOf(queryItems.size()));
        if (queryItems.isEmpty()) {

            return null;
        } else {
            return queryItems;
        }
    }

    /**
     * 通过数据库查找返回图摘查看数据，没有将从网络获取数据
     *
     * @param currentId
     * @return
     */
    public static List<PostItem> getOldImgDataFromRealm(int currentId, Context context) {
        /**
         * 通过数据库查找返回分类查看数据，没有将从网络获取数据
         * @param currentId
         * @param category
         * @param context
         * @return
         */

        int afterId = currentId - DATA_LEGTH;
        if (afterId <= 0) {
            afterId = 0;
        }

        /**
         * 先查找数据
         */
        Realm realm = PostItemRealmHelper.getRealm(context);
        List<PostItem> queryItems = realm.where(PostItem.class)
                .lessThan("id", currentId)
                .greaterThanOrEqualTo("id", afterId)
                .findAllSorted("id", Sort.DESCENDING);

        if (queryItems.isEmpty() || queryItems.size() < DATA_LEGTH) {//数据库中的数据量少于请求量--》请求网络
            //获取数据并写入数据库
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(HttpUtils.getOldImgDataFromServer(currentId));
            realm.commitTransaction();
            //再次查询
            List<PostItem> queryItemsAgain = realm.where(PostItem.class)
                    .lessThan("id", currentId)
                    .greaterThanOrEqualTo("id", afterId)
                    .findAllSorted("id", Sort.DESCENDING);
            return queryItemsAgain;
        } else {
            return queryItems;
        }
    }


    public static List<Explore> getOldExploreDataFromRealm(int currentId, Context context) {
        /**
         * 通过数据库查找返回分类查看数据，没有将从网络获取数据
         * @param currentId
         * @param category
         * @param context
         * @return
         */

        int afterId = currentId - DATA_LEGTH;
        if (afterId <= 0) {
            afterId = 0;
        }

        /**
         * 先查找数据
         */
        Realm realm = ExploreItemRealmHelper.getRealm(context);
        List<Explore> queryItems = realm.where(Explore.class)
                .lessThan("id", currentId)
                .greaterThanOrEqualTo("id", afterId)
                .findAllSorted("id", Sort.DESCENDING);

        if (queryItems.isEmpty() || queryItems.size() < DATA_LEGTH) {//数据库中的数据量少于请求量--》请求网络
            //获取数据并写入数据库
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(HttpUtils.getOldExploreDataFromServer(currentId));
            realm.commitTransaction();
            //再次查询
            List<Explore> queryItemsAgain = realm.where(Explore.class)
                    .lessThan("id", currentId)
                    .greaterThanOrEqualTo("id", afterId)
                    .findAllSorted("id", Sort.DESCENDING);
            return queryItemsAgain;
        } else {
            return queryItems;
        }

    }

    public static List<Explore> getNewExploreDataFromRealm(int currentId, Context context) {
        /**
         * 直接从网络获取数据写入数据库
         */
        Realm realm = ExploreItemRealmHelper.getRealm(context);
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(HttpUtils.getNewExploreDataFromServer(currentId));
        realm.commitTransaction();

        /**
         * 查询数据库并返回数据
         */
        List<Explore> queryItems = realm.where(Explore.class)
                .greaterThan("id", currentId)
                .lessThanOrEqualTo("id", currentId + DATA_LEGTH)
                .findAllSorted("id", Sort.DESCENDING);
        if (queryItems.isEmpty()) {
            return null;
        } else {
            return queryItems;
        }


    }

//TODO:=========================================httoUtils工具如下============================================================================'

    /**
     * 获取服务器分类数据
     *
     * @return
     */
    public static List<Category> getCategoriesDataFromServer() throws IOException {
        List<Category> data = null;
        ReceCategoryJsonData receExploreJsonData = new OkHttpRequest.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader(" User-Agent", GlobalConfig.BROWSER_UA)
                .url(CATEGORY_ADDR)
                .get(ReceCategoryJsonData.class);
        if (receExploreJsonData == null || receExploreJsonData.getCategories() == null) {
            return null;
        }
        data = receExploreJsonData.getCategories();
        return data;
    }

    /**
     * get Category Data From Server
     *
     * @param currentId
     * @param category
     * @return
     */
    public static List<PostItem> getNewPostItemByCategoryFromServer(int currentId, String category) throws IOException {
        String url = "http://www.qingniantuzhai.com/api/posts?since_id=" + currentId + "&count=20&category=" + category + "&";
        List<PostItem> data = null;
        RecItemJsonData recItemJsonData = new OkHttpRequest.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader(" User-Agent", GlobalConfig.BROWSER_UA)
                .url(url)
                .get(RecItemJsonData.class);
        if (recItemJsonData == null || recItemJsonData.getPosts() == null) {
            return null;
        }
        data = recItemJsonData.getPosts();
        return data;
    }

    /**
     * @param currentId
     * @param category
     * @return
     */
    public static List<PostItem> getOldPostItemByCategoryFromServer(int currentId, String category) throws IOException {
        String url = "http://www.qingniantuzhai.com/api/posts?count=20&category=" + category + "&max_id=" + currentId + "&";
        Log.e("url=", url);
        List<PostItem> data = null;

        RecItemJsonData recItemJsonData = new OkHttpRequest.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader("User-Agent", GlobalConfig.BROWSER_UA)
                .url(url)
                .get(RecItemJsonData.class);
        if (recItemJsonData == null || recItemJsonData.getPosts() == null) {
            Log.e("OldPostItemByCategory", "数据为空");
            return null;
        }
        data = recItemJsonData.getPosts();
        return data;
    }


    /**
     * 从服务器获取发现栏目数据-->下拉刷新方式
     *
     * @param currentId
     * @return
     */
    public static List<Explore> getNewExploreDataFromServer(int currentId) throws IOException {

        String url = "http://www.qingniantuzhai.com/api/images?since_id=" + currentId + "&count=20&";
        List<Explore> data = null;

        ReceExploreJsonData receExploreJsonData = new OkHttpRequest.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader(" User-Agent", GlobalConfig.BROWSER_UA)
                .url(url)
                .get(ReceExploreJsonData.class);
        if (receExploreJsonData == null || receExploreJsonData.getImages() == null) {
            return null;
        }
        data = receExploreJsonData.getImages();
        return data;
    }

    /**
     * 从服务器获取发现栏目数据-->上滑刷新
     *
     * @param currentId
     * @return
     */
    public static List<Explore> getOldExploreDataFromServer(int currentId) throws IOException {

        String url = "http://www.qingniantuzhai.com/api/images?max_id=" + currentId + "&count=20&";
        List<Explore> data = null;
        ReceExploreJsonData receExploreJsonData = new OkHttpRequest.Builder()
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader(" User-Agent", GlobalConfig.BROWSER_UA)
                .url(url)
                .get(ReceExploreJsonData.class);
        if (receExploreJsonData == null || receExploreJsonData.getImages() == null) {
            return null;
        }
        data = receExploreJsonData.getImages();
        return data;
    }

    public static List<PostItem> getNewImgDataFromServer(int currentId) throws IOException {


        String url = "http://www.qingniantuzhai.com/api/posts?since_id=" + currentId + "&count=20&";
        Log.e("=-=-=-=-=-=", url);
        List<PostItem> list = null;

        RecItemJsonData data = new OkHttpRequest.Builder()
                .url(url)
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader(" User-Agent", GlobalConfig.BROWSER_UA)
                .get(RecItemJsonData.class);
        if (data == null || data.getPosts() == null) {
            return null;
        }
        list = data.getPosts();
        Log.e("list长度为:", String.valueOf(list.size()));
        return list;
    }

    public static List<PostItem> getOldImgDataFromServer(int lastId) throws IOException {

        String url = "http://www.qingniantuzhai.com/api/posts?count=20&max_id=" + lastId + "&";
        Log.e("=-=-=-=-=-=", url);
        List<PostItem> list = null;

        RecItemJsonData data = new OkHttpRequest.Builder()
                .url(url)
                .addHeader("Connection", "Keep-Alive")
                .addHeader("Accept-Encoding", "gzip")
                .addHeader(" User-Agent", GlobalConfig.BROWSER_UA)
                .get(RecItemJsonData.class);
        if (data == null || data.getPosts() == null) {
            return null;
        }
        list = data.getPosts();
        Log.e("list长度为:", String.valueOf(list.size()));
        return list;
    }
    
    
    
//TOdo:===========================================httoUtils工具如上====================================================

//TODO：============================重载RealmTUils工具如下==============================================================


    /**
     * @param currentId
     * @param category
     * @param context
     * @return 网络错误返回-1
     * 数据库写入错误返回-2
     * 写入完成返回0
     */
    public static int isOldPostItemByCategoryFromRealm(int currentId, String category, Context context) {
        int afterId = currentId - DATA_LEGTH;
        if (afterId <= 0) {
            afterId = 0;
        }
        int flag = 0;
        /**
         * 先查找数据
         */
        Realm realm = PostItemRealmHelper.getRealm(context);
        RealmResults<PostItem> queryItems = realm.where(PostItem.class)
                .equalTo("category_slug", category)
                .lessThan("id", currentId)
                .greaterThanOrEqualTo("id", afterId)
                .findAllSorted("id", Sort.DESCENDING);

        if (queryItems.isEmpty() || queryItems.size() < DATA_LEGTH) {//数据库中的数据量少于请求量--》请求网络
            //获取数据并写入数据库
            try {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(getOldPostItemByCategoryFromServer(currentId, category));
                realm.commitTransaction();
                flag = 0;
            } catch (IOException e) {
                e.printStackTrace();
                flag = -1;
            } catch (RealmError error) {
                flag = -2;
                error.printStackTrace();
            } catch (RealmException error) {
                flag = -2;
                error.printStackTrace();
            }
            //再次查询
            realm.close();
            return flag;
        } else {

            realm.close();
            return flag;
        }
    }

    /**
     * @param currentId
     * @param category
     * @param context
     * @return 网络错误返回-1
     * 数据库写入错误返回-2
     * 写入完成返回0
     */
    public static int isNewPostItemByCategoryFromRealm(int currentId, String category, Context context) {
        int flag = 0;
        Realm realm = PostItemRealmHelper.getRealm(context);
        /**
         * 直接从网络获取数据写入数据库
         */
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(getNewPostItemByCategoryFromServer(currentId, category));
            realm.commitTransaction();
            flag = 0;
        } catch (IOException e) {
            e.printStackTrace();
            flag = -1;
        } catch (RealmError error) {
            flag = -2;
            error.printStackTrace();
        } catch (RealmException error) {
            flag = -2;
            error.printStackTrace();
        }
        realm.close();
        return flag;
    }


    /**
     * 
     * @param currentId
     * @param context
     * @return
     * 网络错误返回-1
     * 数据库写入错误返回-2
     * 写入完成返回0
     */
    public static int isNewImgDataFromRealm(int currentId, Context context) {
        int flag = 0;
        /**
         * 直接从网络获取数据写入数据库
         */
        try {
            Realm realm = PostItemRealmHelper.getRealm(context);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(getNewImgDataFromServer(currentId));
            realm.commitTransaction();
            realm.close();
            flag = 0;
        } catch (IOException e) {
            e.printStackTrace();
            flag = -1;
        } catch (RealmError error) {
            flag = -2;
            error.printStackTrace();
        } catch (RealmException error) {
            flag = -2;
            error.printStackTrace();
        }
        return flag;

    }

    /**
     * 
     * @param currentId
     * @param context
     * @return网络错误返回-1
     * 数据库写入错误返回-2
     * 写入完成返回0
     */
    public static int isOldImgDataFromRealm(int currentId, Context context) {
        int flag = 0;
        int afterId = currentId - DATA_LEGTH;
        if (afterId <= 0) {
            afterId = 0;
        }

        /**
         * 先查找数据
         */

        Realm realm = PostItemRealmHelper.getRealm(context);
        RealmResults<PostItem> queryItems = realm.where(PostItem.class)
                .lessThan("id", currentId)
                .greaterThanOrEqualTo("id", afterId)
                .findAllSorted("id", Sort.DESCENDING);

        if (queryItems.isEmpty() || queryItems.size() < DATA_LEGTH) {//数据库中的数据量少于请求量--》请求网络
            //获取数据并写入数据库
            try {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(getOldImgDataFromServer(currentId));
                realm.commitTransaction();
                flag = 0;
            } catch (IOException e) {
                e.printStackTrace();
                flag = -1;
            } catch (RealmError error) {
                flag = -2;
                error.printStackTrace();
            } catch (RealmException error) {
                flag = -2;
                error.printStackTrace();
            }
            realm.close();
            return flag;
        } else {
            realm.close();
            return flag;
        }
    }


    /**
     * 
     * @param currentId
     * @param context
     * @return
     * 网络错误返回-1
     * 数据库写入错误返回-2
     * 写入完成返回0
     */
    public static int isOldExploreDataFromRealm(int currentId, Context context) {
        int flag = 0;
        int afterId = currentId - DATA_LEGTH;
        if (afterId <= 0) {
            afterId = 0;
        }

        /**
         * 先查找数据
         */
        Realm realm = ExploreItemRealmHelper.getRealm(context);
        RealmResults<Explore> queryItems = realm.where(Explore.class)
                .lessThan("id", currentId)
                .greaterThanOrEqualTo("id", afterId)
                .findAllSorted("id", Sort.DESCENDING);

        if (queryItems.isEmpty() || queryItems.size() < DATA_LEGTH) {//数据库中的数据量少于请求量--》请求网络
            //获取数据并写入数据库
            try {
                realm.beginTransaction();
                realm.copyToRealmOrUpdate(getOldExploreDataFromServer(currentId));
                realm.commitTransaction();
            } catch (IOException e) {
                e.printStackTrace();
                flag = -1;
            } catch (RealmError error) {
                flag = -2;
                error.printStackTrace();
            } catch (RealmException error) {
                flag = -2;
                error.printStackTrace();
            }
            realm.close();
            return flag;

        } else {
            realm.close();
            return flag;
        }

    }

    /**
     * 
     * @param currentId
     * @param context
     * @return网络错误返回-1
     * 数据库写入错误返回-2
     * 写入完成返回0
     */
    public static int isNewExploreDataFromRealm(int currentId, Context context) {
        int flag = 0;
        /**
         * 直接从网络获取数据写入数据库
         */
        Realm realm = ExploreItemRealmHelper.getRealm(context);
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(getNewExploreDataFromServer(currentId));
            realm.commitTransaction();
            flag = 0;
        } catch (IOException e) {
            e.printStackTrace();
            flag = -1;
        } catch (RealmError error) {
            flag = -2;
            error.printStackTrace();
        } catch (RealmException error) {
            flag = -2;
            error.printStackTrace();
        }
        realm.close();
        return flag;
    }

    /**
     * 获取网络数据
     * @param context
     * @return
     */
    public static int isCategoriesDataFromRealm(Context context){
        
        int flag=0;
        
        try {
            Realm realm= TabCategoriesHelper.getRealm(context);
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(getCategoriesDataFromServer());
            realm.commitTransaction();
            realm.close();
            flag=0;
        }catch (IOException e){
            Log.e("网络异常","isCategoriesDataFromRealm："+e.toString());
            return -1;
        }catch (RealmError error) {
            flag = -2;
            error.printStackTrace();
        } catch (RealmException error) {
            flag = -2;
            error.printStackTrace();
        }
        return flag;
    }

}
