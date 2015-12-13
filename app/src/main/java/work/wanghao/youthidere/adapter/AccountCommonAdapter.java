package work.wanghao.youthidere.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import work.wanghao.youthidere.R;
import work.wanghao.youthidere.activity.ImageDisplayActivity;
import work.wanghao.youthidere.model.ReadHistoryItem;
import work.wanghao.youthidere.utils.DateUtils;

/**
 * Created by wangh on 2015-11-30-0030.
 */
public class AccountCommonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {


    private Context context;
    private List<ReadHistoryItem> postItemData;

    public AccountCommonAdapter(Context context) {
        this.context = context;
        postItemData = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment_account_read_history, parent, false);
        view.setOnClickListener(this);
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
        ReadHistoryItem item = postItemData.get(position);
        Log.e("当前的item", "序号：" + position + "id=" + postItemData.get(position).getId());
        holder.itemCreateDate.setText("浏览时间：" + DateUtils.formatDateFromStrWithoutHour(item.getCreated_at()));
        holder.itemTitle.setText(item.getPost().getTitle());
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

    public void updateItem(List<ReadHistoryItem> list) {
        postItemData.clear();
        postItemData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        int posts = postItemData.get(position).getPost().getId();
      
        Log.e("item被点击了", "id号为" + posts + "的item被点击了");
        Intent intent = null;
        intent = new Intent(context, ImageDisplayActivity.class);
        intent.putExtra("post_id", posts);
        context.startActivity(intent);
    }


    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    public static class CommonViewHolder extends RecyclerView.ViewHolder {

        TextView itemCreateDate;
        TextView itemTitle;


        public CommonViewHolder(View itemView) {
            super(itemView);
            itemCreateDate= (TextView) itemView.findViewById(R.id.item_main_card_view_create_time);
            itemTitle= (TextView) itemView.findViewById(R.id.item_main_card_view_title);

        }
    }
}
