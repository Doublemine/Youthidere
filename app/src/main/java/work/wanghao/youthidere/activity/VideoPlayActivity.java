package work.wanghao.youthidere.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.libraries.mediaframework.exoplayerextensions.Video;
import com.google.android.libraries.mediaframework.layeredvideo.PlaybackControlLayer;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.CommentsAdapter;
import work.wanghao.youthidere.adapter.ImaPlayer;
import work.wanghao.youthidere.model.Comments;
import work.wanghao.youthidere.model.RecePostJsonData;
import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.utils.DateUtils;
import work.wanghao.youthidere.utils.HttpUtils;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.SQLiteUtils;

//import com.melnykov.mFabMenu.FloatingActionButton;

/**
 * Created by wangh on 2015-12-6-0006.
 */
public class VideoPlayActivity extends AppCompatActivity implements PlaybackControlLayer.FullscreenCallback, SwipeRefreshLayout.OnRefreshListener {

    private CircleImageView mUserHeaderImg;
    private TextView mUserName;
    private TextView mCreate_at;
    private FrameLayout mVideoContainer;
    private LinearLayout mVideoMainContent;
    private AppBarLayout mVideoAppbarLayout;
    private CollapsingToolbarLayout mVideoCollapsingtoolbar;
    private Toolbar mVideoToolbar;
    private RecyclerView mRecycleComments;
    private SwipeRefreshLayout mRefreshLayout;
    private ImaPlayer imaPlayer;
    private Video mVideo;
    private String category_slug;
    private int post_id;
    private CoordinatorLayout mFabContainer;
    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mFabComments;
    private FloatingActionButton mFabStar;


    private int mScrollOffset = 4;


    private List<Comments> commentsesData;
    private RecePostJsonData totalData;
    private CommentsAdapter adapter;

    private ContentLoadingProgressBar mLoading;

    private void initHeadView() {

        mUserHeaderImg = (CircleImageView) findViewById(R.id.view_video_imageView_avatar);
        mUserName = (TextView) findViewById(R.id.view_video_textView_screen_name);
        mCreate_at = (TextView) findViewById(R.id.view_video_textView_created_at);
        mVideoContainer = (FrameLayout) findViewById(R.id.videoControllerView);
        mVideoMainContent = (LinearLayout) findViewById(R.id.activity_video_main_content);
        mVideoAppbarLayout = (AppBarLayout) findViewById(R.id.activity_video_appbar);
        mVideoCollapsingtoolbar = (CollapsingToolbarLayout) findViewById(R.id.activity_video_collapsing_toolbar);
        mVideoToolbar = (Toolbar) findViewById(R.id.activity_video_tool_bar);
        mRecycleComments = (RecyclerView) findViewById(R.id.activity_video_recycler_view);
        mFabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        mFabComments = (FloatingActionButton) findViewById(R.id.fab_menu_item_comment);
        mFabStar = (FloatingActionButton) findViewById(R.id.fab_menu_item_star);
        mFabContainer = (CoordinatorLayout) findViewById(R.id.fab_container);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        mLoading = (ContentLoadingProgressBar) findViewById(R.id.video_progress);
        mLoading.show();
        initHeadView();
        Intent intent = getIntent();
        category_slug = intent.getStringExtra("category_slug");
        post_id = intent.getIntExtra("post_id", -1);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保存屏幕唤醒

        mVideoToolbar.setTitle(" ");
        setSupportActionBar(mVideoToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mVideoToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        adapter = new CommentsAdapter(this, this);
        mRecycleComments.setLayoutManager(new LinearLayoutManager(this));
        mRecycleComments.setAdapter(adapter);
        mRecycleComments.addOnScrollListener(mOnScrollListener);
        commentsesData = new ArrayList<Comments>();
        initData(post_id);

        mFabComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                createInputCommentsAlert();
            }
        });
        mFabStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                postFavorite();
            }
        });
    }

    @Override
    protected void onStop() {
        if (imaPlayer != null) {
            imaPlayer.pause();
        }

        super.onStop();

    }

    @Override
    protected void onDestroy() {
        if (imaPlayer != null) {
            imaPlayer.release();
        }
        super.onDestroy();

    }


    public void ceateImaPlayer(Video videoItem) {
        if (imaPlayer != null) {
            imaPlayer.release();
        }

        mVideoContainer.removeAllViews();

        imaPlayer = new ImaPlayer(this, mVideoContainer, videoItem, totalData.getPost().getBrief_content());
        imaPlayer.setFullscreenCallback(this);
        imaPlayer.play();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    public void updateComments() {
        AsyncTask<Integer, Void, RecePostJsonData> getDataTask = new AsyncTask<Integer, Void, RecePostJsonData>() {
            @Override
            protected void onPostExecute(RecePostJsonData recePostJsonData) {
                if (recePostJsonData == null || recePostJsonData.getPost() == null) {
                    Snackbar.make(mVideoMainContent, "数据获取失败，请检查网络！", Snackbar.LENGTH_LONG).show();
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
                    if (PreferenceManager.getDefaultSharedPreferences(VideoPlayActivity.this).getBoolean("log_history", true)) {
                        Token token = SQLiteUtils.getCurrentLoginUserToken(VideoPlayActivity.this);
                        if (token != null) {
                            return HttpUtils.getSignglePostDataFromServer(params[0], token.getToken(),true);
                        } else {
                            return HttpUtils.getSignglePostDataFromServer(params[0], null,true);
                        }
                    } else {
                        return HttpUtils.getSignglePostDataFromServer(params[0], null,true);
                    }
                } else {
                    return null;
                }
            }
        };


        getDataTask.execute(post_id);
    }


    private void initData(int id) {
        AsyncTask<Integer, Void, RecePostJsonData> getDataTask = new AsyncTask<Integer, Void, RecePostJsonData>() {
            @Override
            protected void onPostExecute(RecePostJsonData recePostJsonData) {
                if (recePostJsonData == null || recePostJsonData.getPost() == null) {
                    Snackbar.make(mVideoMainContent, "数据获取失败，请检查网络！", Snackbar.LENGTH_LONG).show();
                } else {
                    totalData = recePostJsonData;
                    Glide.with(VideoPlayActivity.this).load(recePostJsonData.getPost().getUser().getAvatar_url()).into(mUserHeaderImg);
                    mUserName.setText("投稿人：" + recePostJsonData.getPost().getUser().getName());
                    mCreate_at.setText("投稿时间："+ DateUtils.formatDateFromStrWithHour(recePostJsonData.getPost().getCreated_at()));
                    mVideo = new Video(recePostJsonData.getPost().getContent(), Video.VideoType.MP4);
                    ceateImaPlayer(mVideo);
                    if (recePostJsonData.getPost().getComments() == null) {
                        return;
                    }
                    commentsesData.removeAll(recePostJsonData.getPost().getComments());
                    commentsesData.addAll(recePostJsonData.getPost().getComments());
                    adapter.updateItem(commentsesData);
                    mVideoMainContent.setVisibility(View.VISIBLE);
                    mLoading.hide();
                }
            }

            @Override
            protected RecePostJsonData doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    if (PreferenceManager.getDefaultSharedPreferences(VideoPlayActivity.this).getBoolean("log_history", true)) {
                        Token token = SQLiteUtils.getCurrentLoginUserToken(VideoPlayActivity.this);
                        if (token != null) {
                            return HttpUtils.getSignglePostDataFromServer(params[0], token.getToken(),true);
                        } else {
                            return HttpUtils.getSignglePostDataFromServer(params[0], null,true);
                        }
                    } else {
                        return HttpUtils.getSignglePostDataFromServer(params[0], null,true);
                    }
                } else {
                    return null;
                }

            }
        };

        getDataTask.execute(id);
    }


    /**
     * 全屏回调事件     
     */

    @Override
    public void onGoToFullscreen() {
        mVideoAppbarLayout.setVisibility(View.GONE);
    }

    @Override
    public void onReturnFromFullscreen() {
        mVideoAppbarLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public void onRefresh() {

    }


    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (Math.abs(dy) > mScrollOffset) {
                if (dy > 0) {
                    mFabMenu.hideMenu(true);
                } else {
                    mFabMenu.showMenu(true);
                }
            }
        }
    };


    private void createInputCommentsAlert() {


        View view = LayoutInflater.from(this).inflate(R.layout.alert_input_comments, null);
        TextView textView = (TextView) view.findViewById(R.id.alert_title);
        final EditText inputComments = (EditText) view.findViewById(R.id.alert_input_content);
        textView.setText("给文章：" + totalData.getPost().getTitle()+"评论");
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
                            updateComments();
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
    
    
    private void postFavorite(){
        AsyncTask<String,Void,Boolean> postFavoriteTask=new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(aBoolean){
                    Snackbar.make(mFabContainer, "收藏成功~", Snackbar.LENGTH_SHORT).show();
                }else {
                    Snackbar.make(mFabContainer, "因为一些原因，收藏失败！", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Boolean doInBackground(String... params) {
                if(NetUtils.isNetConnect()){
                   return HttpUtils.postStarToServer(params[0],params[1]);
                }else {
                    return false;
                }
            }
        };

        Token token = SQLiteUtils.getCurrentLoginUserToken(this);
        if (token == null) {
            Snackbar.make(mFabContainer, "请先登录再进行操作！", Snackbar.LENGTH_SHORT).show();
            return;
        }
        String strToken=token.getToken();
        postFavoriteTask.execute(strToken,String.valueOf(totalData.getPost().getId()));
        
    }
    

}
