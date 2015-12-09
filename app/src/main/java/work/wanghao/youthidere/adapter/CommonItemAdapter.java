package work.wanghao.youthidere.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import work.wanghao.youthidere.R;
import work.wanghao.youthidere.activity.VideoPlayActivity;
import work.wanghao.youthidere.model.PostItem;
import work.wanghao.youthidere.utils.DateUtils;

/**
 * Created by wangh on 2015-11-30-0030.
 */
public class CommonItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    
    
    
    private Context context;
    private List<PostItem> postItemData;

    public CommonItemAdapter(Context context) {
        this.context = context;
        postItemData=new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_main_card_view, parent, false);
        view.setOnClickListener(this);
        final CommonViewHolder commonViewHolder = new CommonViewHolder(view);
        /**
         * TODO： 暂时不设置点击事件
         */
//            commonViewHolder.itemView.setOnClickListener(this);
         
        
        return commonViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final CommonViewHolder commonViewHolder= (CommonViewHolder) holder;
             bindCommonItem(position, commonViewHolder);
    }

    private void bindCommonItem(int position, CommonViewHolder holder) {
        PostItem item=postItemData.get(position);
        Log.e("当前的item", "序号：" + position + "id=" + postItemData.get(position).getId());
        Glide.with(context).load(item.getMain_img()).crossFade().into(holder.preImageView);
        holder.itemCategory.setText(item.getCategory().getName());

        holder.itemCreateDate.setText(DateUtils.formatDateFromStrWithoutHour(item.getCreated_at()));

        holder.itemReadTime.setText(item.getViews_count());
        holder.itemTitle.setText(item.getTitle());
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
    
    public void updateItem(List<PostItem> list){
        postItemData.clear();
        postItemData.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public void onClick(View v) {
        int position= (Integer) v.getTag();
        int posts=postItemData.get(position).getId();
        Log.e("item被点击了", "id号为" + posts + "的item被点击了");
        Intent intent=new Intent(context, VideoPlayActivity.class);
        intent.putExtra("category_slug",postItemData.get(position).getCategory_slug());
        intent.putExtra("post_id",posts);
        context.startActivity(intent);
    }


    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public static class CommonViewHolder extends RecyclerView.ViewHolder{

        public android.widget.ImageView preImageView;
        public android.widget.TextView itemTitle;
        public android.widget.TextView itemCreateDate;
        public android.widget.TextView itemCategory;
        public android.widget.TextView itemReadTime;
        public CommonViewHolder(View itemView) {
            super(itemView);
            preImageView= (android.widget.ImageView) itemView.findViewById(R.id.item_main_card_view_image);
            itemTitle = (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_title);
            itemCreateDate= (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_create_time);
            itemCategory= (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_category);
            itemReadTime= (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_read_time);
        }
    }
}
