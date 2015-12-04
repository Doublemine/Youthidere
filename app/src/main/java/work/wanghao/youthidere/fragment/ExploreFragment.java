package work.wanghao.youthidere.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.RecyclerExploreAdapter;
import work.wanghao.youthidere.model.Explore;
import work.wanghao.youthidere.utils.HttpUtils;
import work.wanghao.youthidere.utils.RealmUtils;

/**
 * Created by wangh on 2015-11-26-0026.
 */
public class ExploreFragment extends Fragment implements Handler.Callback,SwipeRefreshLayout.OnRefreshListener{

    private RecyclerView mRecyclerView;
    private View mView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerExploreAdapter mAdapter;
    private int lastVisibleItem;
    private boolean isLoadMore=false;
    private List<Explore> mdata;
    private int firstItemID;
    private int endItemID;
   
    private static ExploreFragment mExploreFragment;
    public  static  ExploreFragment getInstance(){
        if(mExploreFragment==null){
            mExploreFragment=new ExploreFragment();
        }
        return mExploreFragment;
    }

    public ExploreFragment(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter=new RecyclerExploreAdapter(getActivity());
        mdata=new ArrayList<Explore>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView =inflater.inflate(R.layout.fragment_explore,container,false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.fragment_explore_rv);
        swipeRefreshLayout= (SwipeRefreshLayout) mView.findViewById(R.id.frg_explore_srfl);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);
        
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return mView;
    }

    
    

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mAdapter=new RecyclerExploreAdapter(getActivity());
        mRecyclerView.addOnScrollListener(mScrollListener);
        mRecyclerView.setAdapter(mAdapter);
        initAdapterData();
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    /**
     * 首次载入数据
     */
    private void initAdapterData(){
        loadData(true,0);
    }

    /**
     * 下拉刷新事件
     */
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        loadData(true,firstItemID);
//        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 上滑加载更多数据
     */
    private void loadMore(){
        if(isLoadMore){
            return;
        }
        isLoadMore=true;
        swipeRefreshLayout.setRefreshing(true);
        loadData(false,endItemID);
        
    }

    /**
     * 载入数据函数
     * @param isUpdate 是否是下拉刷新数据
     * @param startId 开始的item的id号
     */
    private void loadData(boolean isUpdate,  int startId){

        /**
         * TODO：这是一个下拉刷新的异步任务
         */
        AsyncTask<Integer, Void, List<Explore>> updateTask = new AsyncTask<Integer, Void, List<Explore>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<Explore> items) {
                if (items == null) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                } else {
                    if (items.size() == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    mdata.addAll(items);
                    mAdapter.updateItem(mdata);
                    swipeRefreshLayout.setRefreshing(false);
                    endItemID = items.get(items.size() - 1).getId();
                    firstItemID = items.get(0).getId();
                }
            }

            @Override
            protected List<Explore> doInBackground(Integer... params) {
                return RealmUtils.getNewExploreDataFromRealm(params[0], getActivity());

            }
        };

        /**
         * TODO：这是一个下滑加载更多的异步任务！
         */
        AsyncTask<Integer, Void, List<Explore>> loadMoreTask = new AsyncTask<Integer, Void, List<Explore>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<Explore> items) {
                if (items == null) {
                    swipeRefreshLayout.setRefreshing(false);
                    isLoadMore = false;
                    return;
                } else {
                    if (items.size() == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                        isLoadMore = false;
                        return;
                    }
                    mdata.addAll(items);
                    mAdapter.updateItem(mdata);
                    swipeRefreshLayout.setRefreshing(false);
                    isLoadMore = false;
                    endItemID = items.get(items.size() - 1).getId();
                    firstItemID = items.get(0).getId();
                    Log.e("updateOldTask数据载入完毕", "--------------------->" + "swipeRefreshLayout=" + String.valueOf(swipeRefreshLayout.isRefreshing()));
                }
            }

            @Override
            protected List<Explore> doInBackground(Integer... params) {
                return RealmUtils.getOldExploreDataFromRealm(params[0],getActivity());

            }
        };
        if(isUpdate){
            updateTask.execute(startId);
        }else {
            loadMoreTask.execute(startId);
        }
    }
    
    /**
     * 监听RecycleView状态的改变
     */
    private RecyclerView.OnScrollListener mScrollListener=new RecyclerView.OnScrollListener(){
        /**
         * 滚动到底部加载更多数据
         * @param recyclerView
         * @param newState
         */
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                swipeRefreshLayout.setRefreshing(true);
                loadMore();//载入更多数据
            }
        }

        /**
         * 获取最后的Item好吗
         * @param recyclerView
         * @param dx
         * @param dy
         */
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        }
    };
}
