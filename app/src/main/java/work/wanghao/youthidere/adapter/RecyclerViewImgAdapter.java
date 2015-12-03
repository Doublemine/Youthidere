package work.wanghao.youthidere.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import work.wanghao.youthidere.R;
import work.wanghao.youthidere.model.PostItem;

/**
 * Created by wangh on 2015-11-26-0026.
 */
public class RecyclerViewImgAdapter extends RecyclerView.Adapter<RecyclerViewImgAdapter.CardViewViewHolder>{
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private  List<PostItem> items;
    
    public RecyclerViewImgAdapter(Context context,List<PostItem> items){
        mContext=context;
        mLayoutInflater=LayoutInflater.from(context);
        this.items=items;
    }
    
    @Override
    public RecyclerViewImgAdapter.CardViewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CardViewViewHolder(mLayoutInflater.inflate(R.layout.item_main_card_view,parent,false));
        
    }

    @Override
    public void onBindViewHolder(RecyclerViewImgAdapter.CardViewViewHolder holder, int position) {
                    PostItem postItem=items.get(position);
                    holder.itemTitle.setText(postItem.getTitle());
        holder.itemReadTime.setText(postItem.getViews_count());
        holder.itemCategory.setText(postItem.getCategory().getSlug());
        holder.itemCreateDate.setText(postItem.getCreated_at());
        Glide.with(mContext).load(postItem.getMain_img()).crossFade().into(holder.preImageView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class CardViewViewHolder extends RecyclerView.ViewHolder {
        public android.widget.ImageView preImageView;
        public android.widget.TextView itemTitle;
        public android.widget.TextView itemCreateDate;
        public android.widget.TextView itemCategory;
        public android.widget.TextView itemReadTime;
        
        public CardViewViewHolder(View itemView) {
            super(itemView);
            preImageView= (android.widget.ImageView) itemView.findViewById(R.id.item_main_card_view_image);
            itemTitle = (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_title);
            itemCreateDate= (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_create_time);
            itemCategory= (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_category);
            itemReadTime= (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_read_time);
        }
        
        
    }


    
}
