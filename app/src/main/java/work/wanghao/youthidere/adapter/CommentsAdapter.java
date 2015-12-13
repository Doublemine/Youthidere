package work.wanghao.youthidere.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.activity.ImageDisplayActivity;
import work.wanghao.youthidere.activity.VideoPlayActivity;
import work.wanghao.youthidere.model.Comments;
import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.utils.HttpUtils;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.SQLiteUtils;

/**
 * Created by wangh on 2015-12-8-0008.
 */
public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Comments> data;
    private VideoPlayActivity videoactivity;
    private ImageDisplayActivity imgactivity;

    public CommentsAdapter(Context context, VideoPlayActivity activity) {
        this.context = context;
        data = new ArrayList<Comments>();
        this.videoactivity = activity;
    }

    public CommentsAdapter(Context context, ImageDisplayActivity activity) {
        this.context = context;
        data = new ArrayList<Comments>();
        this.imgactivity = activity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.view_comment_list_item, parent, false);
//        view.setOnClickListener(this);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommentsViewHolder commonViewHolder = (CommentsViewHolder) holder;

        if (data.size() <= 0 && position == 0) {
            Glide.with(context).load(R.drawable.ic_account_circle_black_18dp).into(commonViewHolder.userAvatar);
            commonViewHolder.userName.setText("暂无评论");
            commonViewHolder.userCommentContents.setText("暂时还没有评论哦，点击浮动按钮即可抢沙发~");
            commonViewHolder.userLikedTime.setVisibility(View.INVISIBLE);
            commonViewHolder.userReply.setVisibility(View.INVISIBLE);
            commonViewHolder.userCommentsTime.setVisibility(View.INVISIBLE);
            commonViewHolder.userLocation.setVisibility(View.INVISIBLE);
            return;
        }
        Comments item = data.get(position);
        Glide.with(context).load(item.getUser().getAvatar_url()).into(commonViewHolder.userAvatar);
        commonViewHolder.userName.setText(item.getUser().getName());
        commonViewHolder.userCommentContents.setText(item.getContent());
        commonViewHolder.userLikedTime.setText(item.getVotes_count());
        commonViewHolder.userCommentsTime.setText(item.getCreated_at());
        commonViewHolder.userLikedTime.setVisibility(View.VISIBLE);
        commonViewHolder.userReply.setVisibility(View.VISIBLE);
        commonViewHolder.userCommentsTime.setVisibility(View.VISIBLE);
        commonViewHolder.userLocation.setVisibility(View.VISIBLE);
        commonViewHolder.userLocation.setText(item.getLocation());
        final int intposition = position;
        commonViewHolder.userReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoactivity == null && imgactivity != null) {
                    createInputCommentsAlert(context, data.get(intposition), imgactivity);
                } else if (videoactivity != null && imgactivity == null) {
                    createInputCommentsAlert(context, data.get(intposition), videoactivity);
                }
               
            }
        });
        commonViewHolder.userLikedTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoactivity == null && imgactivity != null) {
                    vote(data.get(intposition), imgactivity, context);
                } else if (videoactivity != null && imgactivity == null) {
                    vote(data.get(intposition), videoactivity, context);
                }

            }
        });
        commonViewHolder.itemView.setTag(position);
    }


    @Override
    public int getItemCount() {
        if (data.size() == 0) {
            return 1;
        } else {
            return data.size();
        }
    }

//    @Override
//    public void onClick(View v) {
//
//        int postion = (int) v.getTag();
//        createInputCommentsAlert(context, data.get(postion), activity);
//    }

    public void updateItem(List<Comments> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {


        public CircleImageView userAvatar;
        public TextView userName;
        public TextView userCommentsTime;
        public TextView userCommentContents;
        public TextView userLikedTime;
        public TextView userReply;
        public TextView userLocation;


        public CommentsViewHolder(View itemView) {
            super(itemView);
            userAvatar = (CircleImageView) itemView.findViewById(R.id.view_comment_imageView_avatar);
            userName = (TextView) itemView.findViewById(R.id.view_comment_textView_screen_name);
            userCommentsTime = (TextView) itemView.findViewById(R.id.view_comment_textView_created_at);
            userCommentContents = (TextView) itemView.findViewById(R.id.view_comment_textView_content);
            userLikedTime = (TextView) itemView.findViewById(R.id.view_comment_textView_liked_count);
            userReply = (TextView) itemView.findViewById(R.id.view_comment_textView_reply);
            userLocation = (TextView) itemView.findViewById(R.id.view_comment_textView_user_location);

        }
    }


    /**
     * 点击某个item发生的事件
     *
     * @param context
     * @param comments
     */
    private void createInputCommentsAlert(final Context context, final Comments comments, final VideoPlayActivity activity) {


        View view = LayoutInflater.from(context).inflate(R.layout.alert_input_comments, null);
        TextView textView = (TextView) view.findViewById(R.id.alert_title);
        final EditText inputComments = (EditText) view.findViewById(R.id.alert_input_content);
        textView.setText("回复给：@" + comments.getUser().getName());
        AlertDialog.Builder inputCommentAlert = new AlertDialog.Builder(context);

        inputCommentAlert.setTitle("");
        inputCommentAlert.setCancelable(false);
        inputCommentAlert.setView(view);


        Token token = SQLiteUtils.getCurrentLoginUserToken(context);
        if (token == null) {
            Toast.makeText(context, "请先登录再进行操作！", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
                            activity.updateComments();
                        } else {
                            Toast.makeText(context, "评论失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected Boolean doInBackground(String... params) {
                        if (NetUtils.isNetConnect()) {
                            return HttpUtils.postCommentToServer(params[0], params[1], params[2], params[3]);
                        } else {
                            return false;
                        }

//                        return HttpUtils.postVoteToServer(params[0],params[3]);
                    }
                };
                postComment.execute(strToken, commentContent, String.valueOf(comments.getPost_id()), String.valueOf(comments.getId()));
            }
        });
        inputCommentAlert.setNegativeButton("取消", null);
        inputCommentAlert.show();


    }

    private void createInputCommentsAlert(final Context context, final Comments comments, final ImageDisplayActivity activity) {


        View view = LayoutInflater.from(context).inflate(R.layout.alert_input_comments, null);
        TextView textView = (TextView) view.findViewById(R.id.alert_title);
        final EditText inputComments = (EditText) view.findViewById(R.id.alert_input_content);
        textView.setText("回复给：@" + comments.getUser().getName());
        AlertDialog.Builder inputCommentAlert = new AlertDialog.Builder(context);

        inputCommentAlert.setTitle("");
        inputCommentAlert.setCancelable(false);
        inputCommentAlert.setView(view);


        Token token = SQLiteUtils.getCurrentLoginUserToken(context);
        if (token == null) {
            Toast.makeText(context, "请先登录再进行操作！", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(context, "评论成功", Toast.LENGTH_SHORT).show();
                            activity.updateComments();
                        } else {
                            Toast.makeText(context, "评论失败", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected Boolean doInBackground(String... params) {
                        if (NetUtils.isNetConnect()) {
                            return HttpUtils.postCommentToServer(params[0], params[1], params[2], params[3]);
                        } else {
                            return false;
                        }

//                        return HttpUtils.postVoteToServer(params[0],params[3]);
                    }
                };
                postComment.execute(strToken, commentContent, String.valueOf(comments.getPost_id()), String.valueOf(comments.getId()));
            }
        });
        inputCommentAlert.setNegativeButton("取消", null);
        inputCommentAlert.show();


    }

    /**
     * 点赞函数
     */
    private void vote(Comments comments, final VideoPlayActivity activity, final Context context) {
        Token token = SQLiteUtils.getCurrentLoginUserToken(context);
        if (token == null) {
            Toast.makeText(context, "请先登录再进行操作！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String StrToken = token.getToken();
        final int current_user_id = token.getUser().getId();
        AsyncTask<String, Void, Boolean> voteTask = new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    activity.updateComments();
                } else {
                    Toast.makeText(context, "点赞失败~", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Boolean doInBackground(String... params) {
                if (NetUtils.isNetConnect()) {
                    return HttpUtils.postVoteToServer(params[0], params[1], current_user_id);
                } else {
                    return false;
                }
            }
        };
        voteTask.execute(StrToken, String.valueOf(comments.getId()));
    }

    private void vote(Comments comments, final ImageDisplayActivity activity, final Context context) {
        Token token = SQLiteUtils.getCurrentLoginUserToken(context);
        if (token == null) {
            Toast.makeText(context, "请先登录再进行操作！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String StrToken = token.getToken();
        final int current_user_id = token.getUser().getId();
        AsyncTask<String, Void, Boolean> voteTask = new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                    activity.updateComments();
                } else {
                    Toast.makeText(context, "点赞失败~", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected Boolean doInBackground(String... params) {
                if (NetUtils.isNetConnect()) {
                    return HttpUtils.postVoteToServer(params[0], params[1], current_user_id);
                } else {
                    return false;
                }
            }
        };
        voteTask.execute(StrToken, String.valueOf(comments.getId()));
    }


}
