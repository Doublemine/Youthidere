package work.wanghao.youthidere.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.activity.PreViewImageActivity;
import work.wanghao.youthidere.model.Explore;
import work.wanghao.youthidere.utils.DateUtils;

/**
 * Created by wangh on 2015-12-2-0002.
 */
public class RecyclerExploreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context context;
    private List<Explore> exploreData;

    public RecyclerExploreAdapter(Context context) {
        this.context = context;
        this.exploreData = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.card_view_explore_item, parent, false);
//        view.setOnClickListener(this);
        return new ExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Explore item=exploreData.get(position);
        ExploreViewHolder viewHolder= (ExploreViewHolder) holder;
        Glide.with(context).load(item.getPic_small_url()).placeholder(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.ALL).crossFade().into(viewHolder.userUploadImg);
        Glide.with(context).load(item.getUser().getAvatar_url()).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(viewHolder.userHeadImg);
        viewHolder.userName.setText(item.getUser().getName());
        viewHolder.userLocation.setText(item.getLocation());
        viewHolder.createTime.setText(DateUtils.formatDateFromStrWithoutHour(item.getCreated_at()));
        viewHolder.userText.setText(item.getText());
        viewHolder.userUploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, PreViewImageActivity.class);
                intent.putExtra("img_url",exploreData.get(position).getPic_url());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return exploreData.size();
    }

//    @Override
//    public void onClick(View v) {
//        Toast.makeText(context,"功能开发中...",Toast.LENGTH_SHORT).show();
//        Intent intent=new Intent(context, PreViewImageActivity.class);
//        intent.putExtra("img_url",exploreData.get(0).getPic_url());
//    }
//    
    public  static class ExploreViewHolder extends RecyclerView.ViewHolder{
        
        public CircleImageView userHeadImg;
        public TextView userName;
        public TextView createTime;
        public TextView userLocation;
        public TextView userText;
        public GifImageView userUploadImg;

        public ExploreViewHolder(View itemView) {
            super(itemView);
            userHeadImg= (CircleImageView) itemView.findViewById(R.id.card_view_explore_user_image);
            userName= (TextView) itemView.findViewById(R.id.card_view_explore_username);
            createTime= (TextView) itemView.findViewById(R.id.card_view_explore_create_time);
            userLocation= (TextView) itemView.findViewById(R.id.card_view_explore_user_location);
            userText= (TextView) itemView.findViewById(R.id.card_view_explore_user_text);
            userUploadImg= (GifImageView) itemView.findViewById(R.id.card_view_explore_user_upload_image);
        }
    }

    /**
     * 更新数据
     * @param list
     */
    public void updateItem(List<Explore> list){
        exploreData.clear();
        exploreData.addAll(list);
        notifyDataSetChanged();
    }
}
