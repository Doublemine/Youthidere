package work.wanghao.youthidere.utils;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;
import android.util.Pair;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.wangh.http.okhttp.OkHttpClientManager;
import com.wangh.http.okhttp.request.OkHttpRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmObject;
import work.wanghao.youthidere.model.AccountFavorite;
import work.wanghao.youthidere.model.CompleteItemComments;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.model.ReadHistoryItem;
import work.wanghao.youthidere.model.ReceAccountCommentsJsonData;
import work.wanghao.youthidere.model.ReceAccountFavoriteJsonData;
import work.wanghao.youthidere.model.ReceReadHistoryItemJsonData;
import work.wanghao.youthidere.config.GlobalConfig;
import work.wanghao.youthidere.dao.TokenDaoImpl;
import work.wanghao.youthidere.model.Category;
import work.wanghao.youthidere.model.Explore;
import work.wanghao.youthidere.model.Post;
import work.wanghao.youthidere.model.PostItem;
import work.wanghao.youthidere.model.RecItemJsonData;
import work.wanghao.youthidere.model.ReceCategoryJsonData;
import work.wanghao.youthidere.model.ReceExploreJsonData;
import work.wanghao.youthidere.model.ReceFavoriteJsonData;
import work.wanghao.youthidere.model.ReceImageJsonData;
import work.wanghao.youthidere.model.RecePostComment;
import work.wanghao.youthidere.model.RecePostJsonData;
import work.wanghao.youthidere.model.ReceVoteJsonData;
import work.wanghao.youthidere.model.Token;

/**
 * Created by wangh on 2015-11-28-0028.
 */
public class HttpUtils {

    private static final String LOGIN_ADDR = "http://www.qingniantuzhai.com/api/auth/login";
    private static final String RESISTER_ADDR = "http://www.qingniantuzhai.com/api/auth/register";
    private static final String BROWSER_UA = "Mozilla/5.0 (Linux U 10.0; Android 5.0;zh-CN; Doublemine Build/2015) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";
    private static final String CATEGORY_ADDR = "http://www.qingniantuzhai.com/api/categories";
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
            if (SQLiteUtils.getTokenNum(context) <= 0) {
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
     *
     * @return
     */
    public static List<Category> getCategoriesDataFromServer() {
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
     *
     * @param currentId
     * @param category
     * @return
     */
    public static List<PostItem> getNewPostItemByCategoryFromServer(int currentId, String category) {
        String url = "http://www.qingniantuzhai.com/api/posts?since_id=" + currentId + "&count=20&category=" + category + "&";
        List<PostItem> data = null;
        try {
            RecItemJsonData recItemJsonData = new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader(" User-Agent", BROWSER_UA)
                    .url(url)
                    .get(RecItemJsonData.class);
            if (recItemJsonData == null || recItemJsonData.getPosts() == null) {
                return null;
            }
            data = recItemJsonData.getPosts();

        } catch (IOException e) {
            Log.e("NewPostItemByCategory", e.toString());
        }
        return data;
    }

    /**
     * @param currentId
     * @param category
     * @return
     */
    public static List<PostItem> getOldPostItemByCategoryFromServer(int currentId, String category) {
        String url = "http://www.qingniantuzhai.com/api/posts?count=20&category=" + category + "&max_id=" + currentId + "&";
        Log.e("url=", url);
        List<PostItem> data = null;
        try {
            RecItemJsonData recItemJsonData = new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("User-Agent", BROWSER_UA)
                    .url(url)
                    .get(RecItemJsonData.class);
            if (recItemJsonData == null || recItemJsonData.getPosts() == null) {
                Log.e("OldPostItemByCategory", "数据为空");
                return null;
            }
            data = recItemJsonData.getPosts();

        } catch (IOException e) {
            Log.e("OldPostItemByCategory", e.toString());
        }
        return data;
    }


    public static RecePostJsonData getSignglePostDataFromServer(int id) {
        String url = "http://www.qingniantuzhai.com/api/posts/" + id + "?comments=100&";
        RecePostJsonData data = null;
        try {
            RecePostJsonData recePostJsonData = new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("User-Agent", BROWSER_UA)
                    .url(url)
                    .get(RecePostJsonData.class);
            if (recePostJsonData == null || recePostJsonData.getPost() == null) {
                return null;
            }
            data = recePostJsonData;
            if (data.getPost().getCategory_slug().equals("video")) {
                Post temp = data.getPost();
                temp.setContent(JsoupUtils.parseContent2VideoUrl(data.getPost().getContent()));
                data.setPost(temp);
                Log.e("video地址解析为:", ">" + data.getPost().getContent() + "<");
            }

        } catch (IOException e) {
            Log.e("网络异常", "网络异常导致获取数据失败:" + e.toString());
        }
        return data;
    }


    /**
     * 给某一楼层评论回复
     *
     * @param token
     * @param content
     * @param post_id
     * @param parent_id
     * @return
     */
    public static boolean postCommentToServer(String token, String content, String post_id, String parent_id) {
        String url = "http://www.qingniantuzhai.com/api/comments?token=" + token;
        Log.e("url", url);
        boolean Flag = false;
        try {
            RecePostComment commentResponse = new OkHttpRequest.Builder()
                    .url(url)
                    .addParams("content", content)
                    .addParams("parent_id", parent_id)
                    .addParams("post_id", post_id)
                    .addHeader("Content-Type", " application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip;q=1.0,compress;q = 0.5")
                    .addHeader("User-Agent", "Qingniantuzhai/com.qingniantuzhai.ios (3; OS Version 9.1 (Build 13B143))")
                    .addHeader("Accept-Language", "zh-Hans;q= 1.0, en - US; q = 0.9")
                    .post(RecePostComment.class);

            if (commentResponse == null || commentResponse.getComment() == null) {
                Flag = false;
            } else {
                if (content.equals(commentResponse.getComment().getContent())) {
                    Flag = true;
                }
            }
        } catch (IOException e) {

            Log.e("评论提交错误", e.toString());
            Flag = false;
        } catch (Exception e) {
            Log.e("评论提交错误", e.toString());
            Flag = false;
        }
        return Flag;
    }


    /**
     * 给某一篇文章评论
     *
     * @param token
     * @param content
     * @param post_id
     * @return
     */
    public static boolean postSingleCommentToServer(String token, String content, String post_id) {
        String url = "http://www.qingniantuzhai.com/api/comments?token=" + token;
        Log.e("url", url);
        boolean Flag = false;
        try {
            RecePostComment commentResponse = new OkHttpRequest.Builder()
                    .url(url)
                    .addParams("content", content)
                    .addParams("post_id", post_id)
                    .addHeader("Content-Type", " application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip;q=1.0,compress;q = 0.5")
                    .addHeader("User-Agent", "Qingniantuzhai/com.qingniantuzhai.ios (3; OS Version 9.1 (Build 13B143))")
                    .addHeader("Accept-Language", "zh-Hans;q= 1.0, en - US; q = 0.9")
                    .post(RecePostComment.class);

            if (commentResponse == null || commentResponse.getComment() == null) {
                Flag = false;
            } else {
                if (content.equals(commentResponse.getComment().getContent())) {
                    Flag = true;
                }
            }
        } catch (IOException e) {

            Log.e("评论提交错误", e.toString());
            Flag = false;
        } catch (Exception e) {
            Log.e("评论提交错误", e.toString());
            Flag = false;
        }
        return Flag;
    }


    public static boolean postVoteToServer(String token, String comment_id, int current_user_id) {
        boolean flag = false;
        String url = "http://www.qingniantuzhai.com/api/comments/vote?token=" + token;
        try {
            ReceVoteJsonData voteData = new OkHttpRequest.Builder()
                    .url(url)
                    .addParams("comment_id", comment_id)
                    .addHeader("Content-Type", " application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip;q=1.0,compress;q = 0.5")
                    .addHeader("User-Agent", "Qingniantuzhai/com.qingniantuzhai.ios (3; OS Version 9.1 (Build 13B143))")
                    .addHeader("Accept-Language", "zh-Hans;q= 1.0, en - US; q = 0.9")
                    .post(ReceVoteJsonData.class);

            if (voteData == null || voteData.getVote() == null) {
                flag = false;
            } else {

                if (voteData.getVote().getUser_id() == current_user_id) {
                    flag = true;
                }
            }
        } catch (IOException e) {
            flag = false;
            Log.e("点赞错误", "错误内容为:" + e.toString());
        } catch (Exception e) {
            flag = false;
            Log.e("点赞错误", "错误内容为:" + e.toString());
        }
        return flag;
    }

    /**
     * 收藏某一篇文章
     *
     * @return
     */
    public static boolean postStarToServer(String token, String postId) {
        boolean flag = false;
        String url = "http://www.qingniantuzhai.com/api/favorites?token=" + token;
        try {
            ReceFavoriteJsonData voteData = new OkHttpRequest.Builder()
                    .url(url)
                    .addParams("type", "post")
                    .addParams("object_id", postId)
                    .addHeader("Content-Type", " application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip;q=1.0,compress;q = 0.5")
                    .addHeader("User-Agent", "Qingniantuzhai/com.qingniantuzhai.ios (3; OS Version 9.1 (Build 13B143))")
                    .addHeader("Accept-Language", "zh-Hans;q= 1.0, en - US; q = 0.9")
                    .post(ReceFavoriteJsonData.class);

            if (voteData == null || voteData.getFavorite() == null) {
                flag = false;
            } else {
                if (postId.equals(String.valueOf(voteData.getFavorite().getObject_id()))) {
                    flag = true;
                }
            }


        } catch (IOException e) {
            Log.e("收藏错误", e.toString());
            flag = false;
        } catch (Exception e) {
            Log.e("收藏错误", e.toString());
            flag = false;
        }
        return flag;

    }


    /**
     * 上传用户自定义图片到服务器
     *
     * @param token
     * @param text
     * @param imageFile
     * @return
     */
    public static boolean uploadImageToServer(String token, String text, File imageFile) {
        String url = null;
        boolean flag = false;
        if (token.isEmpty()) {
            url = "http://www.qingniantuzhai.com/api/images";
        } else {
            url = "http://www.qingniantuzhai.com/api/images?token=" + token;
        }

        try {

            ReceImageJsonData data = new OkHttpRequest.Builder()
                    .url(url)
                    .addParams("text", text)
                    .addHeader("Content-Type", " application/x-www-form-urlencoded; charset=UTF-8")
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("User-Agent", GlobalConfig.BROWSER_UA)
                    .files(new Pair<String, File>("photo", imageFile))//
                    .upload(ReceImageJsonData.class);

            if (data == null || data.getImage() == null) {
                flag = false;
            } else {
                if (text.equals(data.getImage().getText())) {
                    flag = true;
                }
            }


        } catch (IOException e) {
            Log.e("上传图片错误", "错误详情为:" + e.toString());
            flag = flag;
        } catch (Exception e) {
            flag = false;
        }

        return flag;
    }

    /**
     * 从服务器获取当前用户的阅读历史
     *
     * @param token
     * @param date  格式为:2015-12-10
     * @return
     */
    public static List<ReadHistoryItem> getReadHistoryFromServer(String token, String date) {
        String url = "http://www.qingniantuzhai.com/api/users/read-history?date=" + date + "&token=" + token;
        List<ReadHistoryItem> datas = null;
        try {
            ReceReadHistoryItemJsonData data = new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("User-Agent", GlobalConfig.BROWSER_UA)
                    .url(url)
                    .get(ReceReadHistoryItemJsonData.class);

            if (data == null || data.getHistories() == null) {
                return null;
            } else {
                datas = data.getHistories();
            }
            

        } catch (IOException e) {
            Log.e("获取阅读历史错误", e.toString());
            return null;
        } catch (Exception e) {
            Log.e("获取阅读历史错误", e.toString());
            return null;
        }
        return datas;
    }

    /**
     * 从服务器获取用户的收藏列表
     * @param token
     * @return
     */
    public static List<AccountFavorite> getAccountFavoritesFromServer(String token){
        String url="http://www.qingniantuzhai.com/api/favorites?token="+token+"&type=post";
        List<AccountFavorite> datas=null;
        try {
            ReceAccountFavoriteJsonData data= new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("User-Agent", GlobalConfig.BROWSER_UA)
                    .url(url)
                    .get(ReceAccountFavoriteJsonData.class);
            if(data==null||data.getFavorites()==null){
                return null;
            }else {
                datas=data.getFavorites();
            }
        }catch (IOException e){
            Log.e("获取收藏错误", e.toString());
            return null;
        }catch (Exception e){
            Log.e("获取收藏错误", e.toString()); 
            return null;
        }
        return datas;
    }

    /**
     * TODO:暂时还不知道一页能够容纳多少评论
     * @param page
     * @param token
     * @return
     */
    public static List<CompleteItemComments> getAccountCommentsFromServer(int page,String token){
        String url="http://www.qingniantuzhai.com/api/users/comments?page="+page+"&token="+token;
        List<CompleteItemComments> datas=null;
        try {
            ReceAccountCommentsJsonData data=  new OkHttpRequest.Builder()
                    .addHeader("Connection", "Keep-Alive")
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("User-Agent", GlobalConfig.BROWSER_UA)
                    .url(url)
                    .get(ReceAccountCommentsJsonData.class);
            //// TODO: 2015-12-12-0012 此处内容判断可能会有问题，暂时不想写了就这样吧，有bug再说 
            if(data==null||data.getUser()==null){
                return null;
            }else {
                datas = data.getComments();
            }
        }catch (IOException e){
            Log.e("获取评论错误", e.toString());
            return null;
        }catch (Exception e){
            Log.e("获取评论错误", e.toString());
            return null;
        }
        return datas;
    }
    
}
