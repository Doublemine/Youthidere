package work.wanghao.youthidere.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.CommentsAdapter;
import work.wanghao.youthidere.model.Comments;
import work.wanghao.youthidere.model.RecePostJsonData;
import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.utils.DateUtils;
import work.wanghao.youthidere.utils.HttpUtils;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.SQLiteUtils;

/**
 * Created by wangh on 2015-12-10-0010.
 */
public class ImageDisplayActivity extends AppCompatActivity {


    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mFabComments;
    private FloatingActionButton mFabFarvorite;
    private WebView mWebview;
    private CircleImageView mUserHeaderImg;
    private TextView mUserName;
    private TextView mCreate_at;
    private Toolbar mToolbar;
    private ContentLoadingProgressBar mProgress;
    private RecePostJsonData totalData;
    private LinearLayout mMainContent;
    private CoordinatorLayout mFabContainer;
    private RecyclerView mRecycleView;
    private CommentsAdapter adapter;
    private List<Comments> commentsesData;
    private FloatingActionButton mFabViewComments;

    private String category_slug;
    private int post_id;


    private void initView() {
        mFabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        mFabComments = (FloatingActionButton) findViewById(R.id.fab_menu_item_comment);
        mFabFarvorite = (FloatingActionButton) findViewById(R.id.fab_menu_item_star);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mWebview = (WebView) findViewById(R.id.webview);
        mProgress = (ContentLoadingProgressBar) findViewById(R.id.img_progress);
        mMainContent = (LinearLayout) findViewById(R.id.activity_webview_main_content);
        mUserHeaderImg = (CircleImageView) findViewById(R.id.view_video_imageView_avatar);
        mUserName = (TextView) findViewById(R.id.view_video_textView_screen_name);
        mCreate_at = (TextView) findViewById(R.id.view_video_textView_created_at);
        mFabContainer = (CoordinatorLayout) findViewById(R.id.fab_container);
        mRecycleView = (RecyclerView) findViewById(R.id.activity_video_recycler_view);
        mFabViewComments = (FloatingActionButton) findViewById(R.id.fab_menu_item_view_comments);


    }

    private void initWebview(WebSettings settings) {
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCacheEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        if (NetUtils.isNetConnect()) {
            settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initView();

        commentsesData = new ArrayList<Comments>();
        mWebview.setWebChromeClient(new WebChromeClient());
        mWebview.setVerticalScrollBarEnabled(false);
        mWebview.setHorizontalScrollBarEnabled(false);
        mWebview.addJavascriptInterface(new ShowImageControlInterface(), "xiamo");

        WebSettings settings = mWebview.getSettings();
        initWebview(settings);

        mToolbar.setTitle(" ");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        adapter = new CommentsAdapter(this, this);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(adapter);


        mFabViewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                if (mWebview.getVisibility() == View.VISIBLE) {
                    mWebview.setVisibility(View.GONE);
                    mRecycleView.setVisibility(View.VISIBLE);
                    mFabViewComments.setLabelText("回到图摘");
                    mFabViewComments.setImageResource(R.drawable.ic_insert_photo_white_24dp);
                } else if (mWebview.getVisibility() == View.GONE) {
                    mRecycleView.setVisibility(View.GONE);
                    mWebview.setVisibility(View.VISIBLE);
                    mFabViewComments.setLabelText("查看评论");
                    mFabViewComments.setImageResource(R.drawable.ic_textsms_white_24dp);
                }

            }
        });

        mFabComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                createInputCommentsAlert();
            }
        });
        mFabFarvorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                postFavorite();
            }
        });


        Intent intent = getIntent();
        category_slug = intent.getStringExtra("category_slug");
        post_id = intent.getIntExtra("post_id", -1);
        Log.e("获取到的id",String.valueOf(post_id));
        initData(post_id);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mWebview.removeAllViews();
        mWebview.destroy();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebview.removeAllViews();
        mWebview.destroy();
    }

    private void setWebviewContent(RecePostJsonData data) {
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(getResources().getAssets().open("index.html"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                Log.e("读取循环错误", e.toString());
            } finally {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    Log.e("关闭流错误", e.toString());
                }
            }

            String str = sb.toString();
            str = str.replace("${title_content}", data.getPost().getTitle());
            str = str.replace("${content_create_at}", data.getPost().getCreated_at());
            str = str.replace("${content}", data.getPost().getContent());
            mWebview.loadDataWithBaseURL(null, str, "text/html", "charset=UTF-8", null);

        } catch (IOException e) {
            Log.e("mWebview错误", e.toString());
        }
    }


    private void initData(int id) {
        AsyncTask<Integer, Void, RecePostJsonData> getDataTask = new AsyncTask<Integer, Void, RecePostJsonData>() {
            @Override
            protected void onPostExecute(RecePostJsonData recePostJsonData) {
                if (recePostJsonData == null || recePostJsonData.getPost() == null) {
                    Snackbar.make(mFabContainer, "数据获取失败，请检查网络！", Snackbar.LENGTH_LONG).show();
                } else {
                    totalData = recePostJsonData;
                    Glide.with(ImageDisplayActivity.this).load(totalData.getPost().getUser().getAvatar_url()).into(mUserHeaderImg);
                    mUserName.setText("投稿人：" + totalData.getPost().getUser().getName());
                    mCreate_at.setText("投稿时间：" + DateUtils.formatDateFromStrWithoutHour(totalData.getPost().getCreated_at()));
                    setWebviewContent(recePostJsonData);
//                    mMainContent.setVisibility(View.VISIBLE);
//                    mProgress.hide();
                    if (recePostJsonData.getPost().getComments() == null) {
                        return;
                    }
                    commentsesData.removeAll(recePostJsonData.getPost().getComments());
                    commentsesData.addAll(recePostJsonData.getPost().getComments());
                    adapter.updateItem(commentsesData);
                    mMainContent.setVisibility(View.VISIBLE);
                    mProgress.hide();
                }
            }

            @Override
            protected RecePostJsonData doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    if (PreferenceManager.getDefaultSharedPreferences(ImageDisplayActivity.this).getBoolean("log_history", true)) {
                        Token token = SQLiteUtils.getCurrentLoginUserToken(ImageDisplayActivity.this);
                        if (token != null) {
                            return HttpUtils.getSignglePostDataFromServer(params[0], token.getToken(),false);
                        } else {
                            return HttpUtils.getSignglePostDataFromServer(params[0], null,false);
                        }
                    } else {
                        return HttpUtils.getSignglePostDataFromServer(params[0], null,false);
                    }
                } else {
                    return null;
                }

            }
        };
        getDataTask.execute(id);
    }


    private void createInputCommentsAlert() {


        View view = LayoutInflater.from(this).inflate(R.layout.alert_input_comments, null);
        TextView textView = (TextView) view.findViewById(R.id.alert_title);
        final EditText inputComments = (EditText) view.findViewById(R.id.alert_input_content);
        textView.setText("给文章：" + totalData.getPost().getTitle() + "评论");
        AlertDialog.Builder inputCommentAlert = new AlertDialog.Builder(this);

        inputCommentAlert.setTitle("");
        inputCommentAlert.setCancelable(false);
        inputCommentAlert.setView(view);


        Token token = SQLiteUtils.getCurrentLoginUserToken(this);
        if (token == null) {
            Snackbar.make(mFabContainer, "请先登录再进行操作！", Snackbar.LENGTH_SHORT).show();
            return;
        }

        final String strToken = token.getToken();
        inputCommentAlert.setPositiveButton("提交评论", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String commentContent = inputComments.getText().toString().trim();

                AsyncTask<String, Void, Boolean> postComment = new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            Snackbar.make(mFabContainer, "评论成功~", Snackbar.LENGTH_SHORT).show();

                        } else {
                            Snackbar.make(mFabContainer, "因为一些错误导致评论失败", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected Boolean doInBackground(String... params) {
                        if (NetUtils.isNetConnect()) {
                            return HttpUtils.postSingleCommentToServer(params[0], params[1], params[2]);
                        } else {
                            return false;
                        }
                    }
                };
                postComment.execute(strToken, commentContent, String.valueOf(totalData.getPost().getId()));
            }
        });
        inputCommentAlert.setNegativeButton("取消", null);
        inputCommentAlert.show();
    }


    private void postFavorite() {
        AsyncTask<String, Void, Boolean> postFavoriteTask = new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    Snackbar.make(mFabContainer, "收藏成功~", Snackbar.LENGTH_SHORT).show();
                } else {
                    Snackbar.make(mFabContainer, "因为一些原因，收藏失败！", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Boolean doInBackground(String... params) {
                if (NetUtils.isNetConnect()) {
                    return HttpUtils.postStarToServer(params[0], params[1]);
                } else {
                    return false;
                }
            }
        };

        Token token = SQLiteUtils.getCurrentLoginUserToken(this);
        if (token == null) {
            Snackbar.make(mFabContainer, "请先登录再进行操作！", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String strToken = token.getToken();
        postFavoriteTask.execute(strToken, String.valueOf(totalData.getPost().getId()));

    }

    public void updateComments() {
        AsyncTask<Integer, Void, RecePostJsonData> getDataTask = new AsyncTask<Integer, Void, RecePostJsonData>() {
            @Override
            protected void onPostExecute(RecePostJsonData recePostJsonData) {
                if (recePostJsonData == null || recePostJsonData.getPost() == null) {
                    Snackbar.make(mFabContainer, "数据获取失败，请检查网络！", Snackbar.LENGTH_LONG).show();
                } else {
                    if (recePostJsonData.getPost().getComments() == null) {
                        return;
                    }
                    commentsesData.clear();
                    commentsesData.addAll(recePostJsonData.getPost().getComments());
                    adapter.updateItem(commentsesData);
                }
            }

            @Override
            protected RecePostJsonData doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    if (PreferenceManager.getDefaultSharedPreferences(ImageDisplayActivity.this).getBoolean("log_history", true)) {
                        Token token = SQLiteUtils.getCurrentLoginUserToken(ImageDisplayActivity.this);
                        if (token != null) {
                            return HttpUtils.getSignglePostDataFromServer(params[0], token.getToken(),false);
                        } else {
                            return HttpUtils.getSignglePostDataFromServer(params[0], null,false);
                        }
                    } else {
                        return HttpUtils.getSignglePostDataFromServer(params[0], null,false);
                    }
                } else {
                    return null;
                }
            }
        };


        getDataTask.execute(post_id);
    }

    class ShowImageControlInterface {
        public ShowImageControlInterface() {
        }

        @JavascriptInterface
        public void showImage(String src) {
            Log.e("当前点击图片的地址为:", src);
//             Glide.with(ImageDisplayActivity.this).load(src).downloadOnly(600,600);
            Intent intent = new Intent(ImageDisplayActivity.this, PreViewImageActivity.class);
            intent.putExtra("img_url", src);
            startActivity(intent);
        }
    }
}
