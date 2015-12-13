package work.wanghao.youthidere.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.activity.ImageDisplayActivity;
import work.wanghao.youthidere.model.AccountCommentParent;
import work.wanghao.youthidere.model.CompleteItemComments;
import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.utils.DateUtils;
import work.wanghao.youthidere.utils.SQLiteUtils;

/**
 * Created by wangh on 2015-11-30-0030.
 */
public class AccountCommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {


    private Context context;
    private List<CompleteItemComments> postItemData;
    private Token token;

    public AccountCommentsAdapter(Context context) {
        this.context = context;
        postItemData = new ArrayList<CompleteItemComments>();
        token= SQLiteUtils.getCurrentLoginUserToken(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.view_comment_complete_item, parent, false);
        final CommonViewHolder commonViewHolder = new CommonViewHolder(view);
        /**
         * TODO： 暂时不设置点击事件
         */
        return commonViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CommonViewHolder commonViewHolder = (CommonViewHolder) holder;
        bindCommonItem(position, commonViewHolder);
    }

    private void bindCommonItem(int position, CommonViewHolder holder) {
        final CompleteItemComments item = postItemData.get(position);
        Log.e("当前的item", "序号：" + position + "id=" + postItemData.get(position).getId());
         AccountCommentParent parent=item.getParent();
        if(parent==null){
            holder.parentViewContainer.setVisibility(View.GONE);
        }else {
            holder.parentViewContainer.setVisibility(View.VISIBLE);
            Glide.with(context).load(parent.getUser().getAvatar_url()).into(holder.parentUserAvatar);
            holder.parentCommentDate.setText(DateUtils.formatDateFromStrWithoutHour(parent.getCreated_at()));
            holder.parentUserName.setText(parent.getUser().getName());
            holder.parentUserLocation.setText(parent.getLocation());
            holder.parentUserCommentContent.setText(parent.getContent());
        }
        Glide.with(context).load(token.getUser().getAvatar_url()).into(holder.userAvatar);
        holder.userName.setText(token.getUser().getName());
        holder.userLocation.setText(item.getLocation());
        holder.userCommentContent.setText(item.getContent());
        holder.commentDate.setText(DateUtils.formatDateFromStrWithoutHour(item.getCreated_at()));
        holder.artcle.setText(item.getPost().getTitle());
        holder.artcle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ImageDisplayActivity.class);//此处需要一个公共解析Activity
                intent.putExtra("post_id",item.getPost().getId());
                context.startActivity(intent);
            }
        });
        holder.itemView.setTag(position);
        

    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.clearAnimation();
    }


    @Override
    public int getItemCount() {
        return postItemData.size();
    }

    public void updateItem(List<CompleteItemComments> list) {
        postItemData.clear();
        postItemData.addAll(list);
        notifyDataSetChanged();
    }



    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public static class CommonViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userAvatar;
        TextView userName;
        TextView commentDate;
        TextView userLocation;
        TextView userCommentContent;
        LinearLayout parentViewContainer;
        CircleImageView parentUserAvatar;
        TextView parentUserName;
        TextView parentCommentDate;
        TextView parentUserLocation;
        TextView parentUserCommentContent;
        TextView artcle;



        public CommonViewHolder(View itemView) {
            super(itemView);
            userAvatar = (CircleImageView) itemView.findViewById(R.id.view_comment_imageView_avatar);
            userName = (TextView) itemView.findViewById(R.id.view_comment_textView_screen_name);
            commentDate = (TextView) itemView.findViewById(R.id.view_comment_textView_created_at);
            userLocation = (TextView) itemView.findViewById(R.id.view_comment_textView_user_location);
            userCommentContent = (TextView) itemView.findViewById(R.id.view_comment_textView_content);
            parentViewContainer = (LinearLayout) itemView.findViewById(R.id.parent_view_container);
            parentUserAvatar = (CircleImageView) itemView.findViewById(R.id.parent_view_comment_imageView_avatar);
            parentUserName = (TextView) itemView.findViewById(R.id.parent_view_comment_textView_screen_name);
            parentCommentDate = (TextView) itemView.findViewById(R.id.parent_view_comment_textView_created_at);
            parentUserLocation = (TextView) itemView.findViewById(R.id.parent_view_comment_textView_user_location);
            parentUserCommentContent = (TextView) itemView.findViewById(R.id.parent_view_comment_textView_content);
            artcle= (TextView) itemView.findViewById(R.id.view_comment_textView_artcle);
        }
    }
}
