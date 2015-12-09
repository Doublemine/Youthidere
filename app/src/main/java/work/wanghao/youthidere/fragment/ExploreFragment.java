package work.wanghao.youthidere.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
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

import io.realm.Realm;
import io.realm.Sort;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.RecyclerExploreAdapter;
import work.wanghao.youthidere.db.ExploreItemRealmHelper;
import work.wanghao.youthidere.model.Explore;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.RealmUtils;

/**
 * Created by wangh on 2015-11-26-0026.
 */
public class ExploreFragment extends Fragment implements Handler.Callback, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private View mView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerExploreAdapter mAdapter;
    private int lastVisibleItem;
    private boolean isLoadMore = false;
    private List<Explore> mdata;
    private int firstItemID;
    private int endItemID;

    private Realm mRealm;

    private boolean isInit;
    private static ExploreFragment mExploreFragment;

    public static ExploreFragment getInstance() {
        if (mExploreFragment == null) {
            mExploreFragment = new ExploreFragment();
        }
        return mExploreFragment;
    }

    public ExploreFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInit=true;
        mAdapter = new RecyclerExploreAdapter(getActivity());
        mdata = new ArrayList<Explore>();
        mRealm = ExploreItemRealmHelper.getRealm(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_explore, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.fragment_explore_rv);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.frg_explore_srfl);
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

        SlideInBottomAnimationAdapter scaleInAnimationAdapter=new SlideInBottomAnimationAdapter(mAdapter);
        scaleInAnimationAdapter.setFirstOnly(false);
//        scaleInAnimationAdapter.setDuration(800);
        mRecyclerView.setAdapter(scaleInAnimationAdapter);
        initAdapterData();
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    /**
     * 首次载入数据
     */
    private void initAdapterData() {
        if(isInit){//载入cache
            Number number=mRealm.where(Explore.class).max("id");
            if(number==null){//本地没有缓存--->请求网络数据
                if(NetUtils.isNetConnect()){
                    loadData(true,0);
                }else {
                    Snackbar.make(mView,"无网络连接，无法从服务器上获取数据，请检查网络!",Snackbar.LENGTH_INDEFINITE).show();
                }
                
                
            }else {//本地有缓存--->加载缓存
               
                List<Explore> initData=mRealm.where(Explore.class)
                        .lessThanOrEqualTo("id",number.intValue())
                        .greaterThan("id",number.intValue()-20)
                        .findAllSorted("id",Sort.DESCENDING);
                
                if(initData==null||initData.size()<=0){//没有数据---不太可能
                    if(NetUtils.isNetConnect()){//有网络-->请求数据
                        Snackbar.make(mView,"你遇到了一个不常见的Bug，请访问：notes.wanghao.work反馈给作者吧:)",Snackbar.LENGTH_LONG).show();
                    }else {//无网络
                        Snackbar.make(mView,"你遇到了一个不常见的Bug，请访问：notes.wanghao.work反馈给作者吧:)",Snackbar.LENGTH_LONG).show();
                    }
                }else {//有数据
                    mdata.removeAll(initData);
                    mdata.addAll(initData);
                    mAdapter.updateItem(mdata);
                    endItemID=initData.get(initData.size()-1).getId();
                    firstItemID=initData.get(0).getId();
                    isInit=false;
                    //到这里缓存加载完毕接下来 看有无网络进行更新数据
                    if(NetUtils.isNetConnect()){//有网络--》更新数据
                        loadData(true,number.intValue());
                    }else {//无网络
                        Snackbar.make(mView,"无网络连接，先看看本地缓存吧!",Snackbar.LENGTH_INDEFINITE).show();
                        return;
                    }
                }
            }
            
        }
    }

    /**
     * 下拉刷新事件
     */
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        loadData(true, firstItemID);
//        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 上滑加载更多数据
     */
    private void loadMore() {
        if (isLoadMore) {
            return;
        }
        isLoadMore = true;
        Log.e("开始载入更多",String.valueOf(endItemID));
        loadData(false, endItemID);

    }

    /**
     * 载入数据函数
     *
     * @param isUpdate 是否是下拉刷新数据
     * @param startId  开始的item的id号
     */
    private void loadData(boolean isUpdate, final int startId) {


        AsyncTask<Integer, Void, Integer> loadNewDataFromRealm = new AsyncTask<Integer, Void, Integer>() {


            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(Integer integer) {
                List<Explore> realmData = null;
                if (integer == -1) {//表示网络错误，没有获取到新的数据直接返回
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(mView,"无网络连接，无法从服务器上获取数据，请检查网络!",Snackbar.LENGTH_SHORT).show();
                    return;
                } else {//有网络连接
                    if(startId==0){
                        int maxid=mRealm.where(Explore.class).max("id").intValue();
                        realmData = mRealm.where(Explore.class)
                                .lessThanOrEqualTo("id", maxid)
                                .greaterThan("id",maxid -20)
                                .findAllSorted("id", Sort.DESCENDING);
                        if(realmData.size()>20){
                            realmData=realmData.subList(0,19);
                        }
                    }else {
                        realmData = mRealm.where(Explore.class)
                                .greaterThan("id", startId)
                                .lessThanOrEqualTo("id", startId + 20)
                                .findAllSorted("id", Sort.DESCENDING);
                    }
                    
                }
               
                if (realmData==null || realmData.size() == 0) {
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(mView,"已经是最新的数据咯~",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Log.e("loadData", "Expolre获取更新数据完成，数据量:" + String.valueOf(realmData.size()));
                mdata.removeAll(realmData);//去掉重复元素
                mdata.addAll(0, realmData);//插入到最前面
                mAdapter.updateItem(mdata);
                swipeRefreshLayout.setRefreshing(false);
                endItemID = realmData.get(realmData.size() - 1).getId();
                firstItemID = realmData.get(0).getId();
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    return RealmUtils.isNewExploreDataFromRealm(startId, getActivity());
                } else {
                    return -1;
                }
            }
        };


        AsyncTask<Integer, Void, Integer> loadOldDataFromRealm = new AsyncTask<Integer, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                List<Explore> realmData = null;
                realmData = mRealm.where(Explore.class)
                        .lessThan("id", startId)
                        .greaterThanOrEqualTo("id", startId - 20)
                        .findAllSorted("id", Sort.DESCENDING);
               
                if (realmData==null|| realmData.size() == 0) {
                    swipeRefreshLayout.setRefreshing(false);
                    isLoadMore=false;
                    if(integer==-1){
                        Snackbar.make(mView,"没有网络，加载不出来更多数据...",Snackbar.LENGTH_SHORT).show();
                    }else {
                        Snackbar.make(mView,"已经到底咯~",Snackbar.LENGTH_SHORT).show();
                    }
                    return;
                }
                Log.e("loadData", "Explore加载更多数据完成，数据量：" + String.valueOf(realmData.size()));
                mdata.removeAll(realmData);
                mdata.addAll(realmData);
                mAdapter.updateItem(mdata);
                swipeRefreshLayout.setRefreshing(false);
                isLoadMore = false;
                endItemID = realmData.get(realmData.size() - 1).getId();
                isLoadMore=false;
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    Log.e("有网络","开始载入网络数据");
                    return RealmUtils.isOldExploreDataFromRealm(startId, getActivity());
                } else {
                    Log.e("无网络","无法载入网络数据");
                    return -1;
                }
            }
        };


        if (isUpdate) {
            loadNewDataFromRealm.execute(startId);
        } else {
            loadOldDataFromRealm.execute(startId);
        }
    }

    /**
     * 监听RecycleView状态的改变
     */
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        /**
         * 滚动到底部加载更多数据
         * @param recyclerView
         * @param newState
         */
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
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
