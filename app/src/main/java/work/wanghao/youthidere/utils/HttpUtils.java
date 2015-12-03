package work.wanghao.youthidere.utils;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wangh.http.okhttp.OkHttpClientManager;
import com.wangh.http.okhttp.request.OkHttpRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.dao.TokenDaoImpl;
import work.wanghao.youthidere.model.Category;
import work.wanghao.youthidere.model.Explore;
import work.wanghao.youthidere.model.PostItem;
import work.wanghao.youthidere.model.RecItemJsonData;
import work.wanghao.youthidere.model.ReceCategoryJsonData;
import work.wanghao.youthidere.model.ReceExploreJsonData;
import work.wanghao.youthidere.model.Token;

/**
 * Created by wangh on 2015-11-28-0028.
 */
public class HttpUtils {

    private static final String LOGIN_ADDR = "http://www.qingniantuzhai.com/api/auth/login";
    private static final String RESISTER_ADDR = "http://www.qingniantuzhai.com/api/auth/register";
    private static final String BROWSER_UA = "Mozilla/5.0 (Linux U 10.0; Android 5.0;zh-CN; Doublemine Build/2015) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
    private static final String CATEGORY_ADDR="http://www.qingniantuzhai.com/api/categories";
    private static Gson gson;

    public static Gson getGsonInstance() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .create();
        }
        return gson;
    }

    /**
     * 同步登录
     *
     * @param email
     * @param password
     * @param context
     * @param UA
     * @return
     */
    public static int httpLogin(String email, String password, final Context context, String UA) {
        int result = -1;
        try {
            Log.e("执行了我", "----" + result + "-----");
            Token response = new OkHttpRequest.Builder()
                    .url(LOGIN_ADDR)
                    .addParams("email", email)
                    .addParams("password", password)
                    .addHeader("Content-Type", " application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", context.getResources().getString(R.string.browser_ua))
                    .post(Token.class);

            Log.e("登录token", response.toString());
            if (response.getError() != null) {
                Log.e("执行了我", "----" + result + "-----");
                return result;
            }

            if (DbUtils.getTokenNum(context) <= 0) {
                TokenDaoImpl.getInstance(context).insertToken(response);
                result = 0;
            } else {
                if (TokenDaoImpl.getInstance(context).selectToken(response).getUser().getId() == response.getUser().getId()) {
                    Log.e("------已经存在------->", "进行更新");
                    TokenDaoImpl.getInstance(context).updateToken(response);
                    result = 0;
                } else {
                    Log.e("------不存在------->", "进行插入");
                    TokenDaoImpl.getInstance(context).insertToken(response);
                    result = 0;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }

        return result;
    }


    /**
     * 同步注册
     *
     * @param username
     * @param email
     * @param password
     * @param context
     * @param UA
     * @return
     */

    public static int httpRegister(String username, String email, String password, final Context context, String UA) {
        int result = -1;
        try {
            Token response = new OkHttpRequest.Builder()
                    .url(RESISTER_ADDR)
                    .addParams("email", email)
                    .addParams("password", password)
                    .addParams("name", username)
                    .addHeader("Content-Type", " application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", UA)
                    .post(Token.class);

            if (response.getError() != null) {//用户已经被注册
                return -1;
            } else if (response.getMessage() != null && response.getStatus_code() == 403) {
                return -2;
            }


            TokenDaoImpl.getInstance(context).insertToken(response);
            result = 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 2;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
        return result;
    }

    /**
     * TODO:以下方法为测试方法，用于RecycleView的更新数据逻辑测试，不要在正式版中调用以下方法
     */
    public static List<PostItem> getNewImgDataFromServer(int currentId) {


        String url = "http://www.qingniantuzhai.com/api/posts?since_id=" + currentId + "&count=20&";
        Log.e("=-=-=-=-=-=", url);
        List<PostItem> list = null;
        try {
            RecItemJsonData data = new OkHttpRequest.Builder()
                    .url(url)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", BROWSER_UA)
                    .get(RecItemJsonData.class);
            if (data == null || data.getPosts() == null) {
                return null;
            }
            list = data.getPosts();
            Log.e("list长度为:", String.valueOf(list.size()));
        } catch (Exception e) {
            Log.e("转换异常", "");
            e.printStackTrace();

        }
        return list;
    }

    public static List<PostItem> getOldImgDataFromServer(int lastId) {

        String url = "http://www.qingniantuzhai.com/api/posts?count=20&max_id=" + lastId + "&";
        Log.e("=-=-=-=-=-=", url);
        List<PostItem> list = new ArrayList<>();
        try {
            RecItemJsonData data = new OkHttpRequest.Builder()
                    .url(url)
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", BROWSER_UA)
                    .get(RecItemJsonData.class);
            if (data == null || data.getPosts() == null) {
                return null;
            }
            list = data.getPosts();
            Log.e("list长度为:", String.valueOf(list.size()));
        } catch (Exception e) {
            Log.e("转换异常", e.toString());
        }
        return list;
    }


    @Deprecated
    /**
     * @param lastId
     * @param UA
     * @param realm
     * @return
     */
    public static List<PostItem> getUpdateOld__(int lastId, String UA, Realm realm) {
        String url = "http://www.qingniantuzhai.com/api/posts?count=20&max_id=" + lastId + "&";
        Gson gson = HttpUtils.getGsonInstance();
        Log.e("=-=-=-=-=-=", url);
        List<PostItem> list = null;
        try {
            OkHttpClient client = OkHttpClientManager.getInstance().getOkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", UA)
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e("致命错误", "Okhttp执行错误");
                return null;
            } else {
                RecItemJsonData items = gson.fromJson(response.body().string(), RecItemJsonData.class);
                list = items.getPosts();
                realm.beginTransaction();
                realm.copyToRealm(list);
                realm.commitTransaction();
            }
        } catch (Exception e) {
            Log.e("转换异常", e.toString());
        }
        return list;
    }

    @Deprecated
    public static List<PostItem> getUpdateNew__(int currentId, String UA) {

        Gson gson = HttpUtils.getGsonInstance();
        String url = "http://www.qingniantuzhai.com/api/posts?since_id=" + currentId + "&count=20&";
        Log.e("=-=-=-=-=-=", url);
        List<PostItem> list = null;
        try {
            OkHttpClient client = OkHttpClientManager.getInstance().getOkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", UA)
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e("致命错误", "Okhttp执行错误");
                return null;
            } else {
//                Log.e("-->",response.body().string());
                RecItemJsonData items = gson.fromJson(response.body().string(), RecItemJsonData.class);
                Log.e("-->", String.valueOf(items.getPosts().size()));
                list = items.getPosts();
//                realm.beginTransaction();
//                realm.copyToRealm(list);
//                realm.commitTransaction();
            }


        } catch (Exception e) {
            Log.e("转换异常", "");
            e.printStackTrace();

        }
        return list;
    }

    /**
     * 从服务器获取发现栏目数据-->下拉刷新方式
     *
     * @param currentId
     * @return
     */
    public static List<Explore> getNewExploreDataFromServer(int currentId) {

        String url = "http://www.qingniantuzhai.com/api/images?since_id=" + currentId + "&count=20&";
        List<Explore> data = null;
        try {
            ReceExploreJsonData receExploreJsonData = new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", BROWSER_UA)
                    .url(url)
                    .get(ReceExploreJsonData.class);
            if (receExploreJsonData == null || receExploreJsonData.getImages() == null) {
                return null;
            }
            data = receExploreJsonData.getImages();
        } catch (IOException explore) {
            Log.e("下拉刷新获取发现数据", "错误:" + explore.toString());
        }
        return data;
    }

    /**
     * 从服务器获取发现栏目数据-->上滑刷新
     *
     * @param currentId
     * @return
     */
    public static List<Explore> getOldExploreDataFromServer(int currentId) {

        String url = "http://www.qingniantuzhai.com/api/images?max_id=" + currentId + "&count=20&";
        List<Explore> data = null;
        try {
            ReceExploreJsonData receExploreJsonData = new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", BROWSER_UA)
                    .url(url)
                    .get(ReceExploreJsonData.class);
            if (receExploreJsonData == null || receExploreJsonData.getImages() == null) {
                return null;
            }
            data = receExploreJsonData.getImages();
        } catch (IOException explore) {
            Log.e("下滑加载更多获取发现数据", "错误:" + explore.toString());
        }
        return data;
    }

    /**
     * 获取服务器分类数据
     * @return
     */
    public static List<Category> getCategoriesDataFromServer(){
        List<Category> data = null;
        try {
            ReceCategoryJsonData receExploreJsonData = new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", BROWSER_UA)
                    .url(CATEGORY_ADDR)
                    .get(ReceCategoryJsonData.class);
            if (receExploreJsonData == null || receExploreJsonData.getCategories() == null) {
                return null;
            }
            data = receExploreJsonData.getCategories();
        } catch (IOException explore) {
            Log.e("获取分类数据出错", "错误:" + explore.toString());
        }
        return data;
    }

    /**
     * get Category Data From Server
     * @param currentId
     * @param category
     * @return
     */
    public static List<PostItem> getNewPostItemByCategoryFromServer(int currentId,String category){
       String url="http://www.qingniantuzhai.com/api/posts?since_id="+currentId+"&count=20&category="+category+"&";
        List<PostItem> data=null;
        try{
            RecItemJsonData recItemJsonData=new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", BROWSER_UA)
                    .url(url)
                    .get(RecItemJsonData.class);
            if(recItemJsonData==null||recItemJsonData.getPosts()==null){
                return null;
            }
            data=recItemJsonData.getPosts();
            
        }catch (IOException e){
            Log.e("NewPostItemByCategory",e.toString());
        }
        return data;
    }

    /**
     * 
     * @param currentId
     * @param category
     * @return
     */
    public static List<PostItem> getOldPostItemByCategoryFromServer(int currentId,String category){
        String url="http://www.qingniantuzhai.com/api/posts?count=20&category="+category+"&max_id="+currentId+"&";
        Log.e("url=",url);
        List<PostItem> data=null;
        try{
            RecItemJsonData recItemJsonData=new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("User-Agent", BROWSER_UA)
                    .url(url)
                    .get(RecItemJsonData.class);
            if(recItemJsonData==null||recItemJsonData.getPosts()==null){
                Log.e("OldPostItemByCategory","数据为空");
                return null;
            }
            data=recItemJsonData.getPosts();

        }catch (IOException e){
            Log.e("OldPostItemByCategory",e.toString());
        }
        return data;
    }

}
