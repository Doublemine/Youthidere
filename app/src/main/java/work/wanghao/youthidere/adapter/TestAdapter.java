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
 * Created by wangh on 2015-12-1-0001.
 */
public class TestAdapter extends ArrayRecycleAdapter<PostItem,TestAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;

    public TestAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
//        return super.getItemId(position);
        return get(position).getId();
    }

   

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.item_main_card_view,parent,false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position, List<Object> payloads) {
//        super.onBindViewHolder(holder, position, payloads);
        PostItem item=get(position);
        holder.itemTitle.setText(item.getTitle());
        holder.itemReadTime.setText(item.getViews_count());
        holder.itemCategory.setText(item.getCategory().getSlug());
        holder.itemCreateDate.setText(item.getCreated_at());
        Glide.with(context).load(item.getMain_img()).into(holder.preImageView);
        
        
    }

    public int getFirstId(){
        return (int)getItemId(0);
    }
    
    public int getEndId(){
        return (int)getItemId(size()-1);
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        public android.widget.ImageView preImageView;
        public android.widget.TextView itemTitle;
        public android.widget.TextView itemCreateDate;
        public android.widget.TextView itemCategory;
        public android.widget.TextView itemReadTime;

        public ViewHolder(View itemView) {
            super(itemView);
            
            
            preImageView= (android.widget.ImageView) itemView.findViewById(R.id.item_main_card_view_image);
            itemTitle = (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_title);
            itemCreateDate= (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_create_time);
            itemCategory= (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_category);
            itemReadTime= (android.widget.TextView) itemView.findViewById(R.id.item_main_card_view_read_time);
        }
    }
}
