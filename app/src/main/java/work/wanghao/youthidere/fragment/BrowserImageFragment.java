package work.wanghao.youthidere.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
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
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.CommonItemAdapter;
import work.wanghao.youthidere.db.PostItemRealmHelper;
import work.wanghao.youthidere.model.PostItem;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.RealmUtils;

/**
 * Created by wangh on 2015-11-26-0026.
 * 为主页图摘选项卡Fragment
 */
public class BrowserImageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private View mView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private CommonItemAdapter mCommonItemAdapter;
    private boolean isLoadMore = false;
    private static BrowserImageFragment mBrowserImageFragment;
    private int lastVisibleItem;
    private int firstItemID;
    private int endItemID;
    private Realm mRealm;
    private boolean isInit;

    private List<PostItem> adapterData = new ArrayList<PostItem>();


    public static BrowserImageFragment getInstance() {
        if (mBrowserImageFragment == null) {
            mBrowserImageFragment = new BrowserImageFragment();
        }
        return mBrowserImageFragment;
    }

    public static List<PostItem> items = new ArrayList<>();

    public BrowserImageFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInit=true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_browser_image, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.fragment_browser_image_rv);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.frg_browser_image_srfl);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(this);

        return mView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRealm = PostItemRealmHelper.getRealm(getActivity());
        mCommonItemAdapter = new CommonItemAdapter(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        ScaleInAnimationAdapter scaleInAnimationAdapter=new ScaleInAnimationAdapter(mCommonItemAdapter);
        scaleInAnimationAdapter.setFirstOnly(false);
//        scaleInAnimationAdapter.setDuration(600);
        mRecyclerView.setAdapter(scaleInAnimationAdapter);
        Log.e("mRealm", "realm对象已被创建");
        initAdapterData();
    }

    @Override
    public void onRefresh() {
        loadData(firstItemID, true);;
    }
    
    private void loadMore() {
        if (isLoadMore) {
            return;
        }
        isLoadMore = true;
        loadData(endItemID, false);
    }


    private void initAdapterData() {
        if (isInit) {//载入cache
            Number number = mRealm.where(PostItem.class).max("id");
            if (number == null) {//本地没有缓存--->请求网络数据
                if (NetUtils.isNetConnect()) {
                    loadData(0, true);
                } else {
                    Snackbar.make(mView, "无网络连接，无法从服务器上获取数据，请检查网络!", Snackbar.LENGTH_INDEFINITE).show();
                }
                
            } else {//本地有缓存--->加载缓存

                List<PostItem> initData = mRealm.where(PostItem.class)
                        .lessThanOrEqualTo("id", number.intValue())
                        .greaterThan("id", number.intValue() - 20)
                        .findAllSorted("id", Sort.DESCENDING);

                if (initData == null || initData.size() <= 0) {//没有数据---不太可能
                    if (NetUtils.isNetConnect()) {//有网络-->请求数据
                        Snackbar.make(mView, "你遇到了一个不常见的Bug，请访问：notes.wanghao.work反馈给作者吧:)", Snackbar.LENGTH_LONG).show();
                    } else {//无网络
                        Snackbar.make(mView, "你遇到了一个不常见的Bug，请访问：notes.wanghao.work反馈给作者吧:)", Snackbar.LENGTH_LONG).show();
                    }
                } else {//有数据
                    adapterData.removeAll(initData);
                    adapterData.addAll(initData);
                    mCommonItemAdapter.updateItem(adapterData);
                    endItemID = initData.get(initData.size() - 1).getId();
                    firstItemID = initData.get(0).getId();
                    isInit = false;
                    //到这里缓存加载完毕接下来 看有无网络进行更新数据
                    if (NetUtils.isNetConnect()) {//有网络--》更新数据
                        loadData(number.intValue(), true);
                    } else {//无网络
                        Snackbar.make(mView, "无网络连接，先看看本地缓存吧!", Snackbar.LENGTH_INDEFINITE).show();
                        return;
                    }
                }
            }

        }


    }



    private void loadData(final int startPage, boolean isNew) {
        Log.e("开始载入数据", "--------------------->" + "startPage=" + startPage + "isNew=" + String.valueOf(isNew));
        AsyncTask<Integer, Void, Integer> loadNewDatafromRealmTask = new AsyncTask<Integer, Void, Integer>() {

            @Override
            protected void onPreExecute() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                List<PostItem> realmData = null;
                if(integer==-1){
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(mView,"无网络连接，无法从服务器上获取数据，请检查网络!",Snackbar.LENGTH_SHORT).show();
                    return;
                }else {//此处需要判定是否为0，否则会取不到数据
                    if(startPage==0){
                        int maxid=mRealm.where(PostItem.class).max("id").intValue();
                        realmData = mRealm.where(PostItem.class)
                                .lessThanOrEqualTo("id", maxid)
                                .greaterThan("id",maxid -20)
                                .findAllSorted("id", Sort.DESCENDING);
                        if(realmData.size()>20){
                            realmData=realmData.subList(0,19);
                        }
                    }else {
                        realmData = mRealm.where(PostItem.class)
                                .greaterThan("id", startPage)
                                .lessThanOrEqualTo("id", startPage + 20)
                                .findAllSorted("id", Sort.DESCENDING);
                    }
                }
                if(realmData==null||realmData.size()<=0){
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(mView,"已经是最新的数据咯~",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                adapterData.removeAll(realmData);
                adapterData.addAll(0, realmData);
                mCommonItemAdapter.updateItem(adapterData);
                swipeRefreshLayout.setRefreshing(false);
                endItemID=realmData.get(realmData.size()-1).getId();
                firstItemID=realmData.get(0).getId();
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    Log.e("网络连接", "网络已连接");
                    return RealmUtils.isNewImgDataFromRealm(params[0], getActivity());
                } else {
                    return -1;
                }

            }
        };

        AsyncTask<Integer, Void, Integer> loadOldDatafromRealmTask = new AsyncTask<Integer, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                List<PostItem> realmData=null;
                realmData=mRealm.where(PostItem.class)
                        .lessThan("id",startPage)
                        .greaterThanOrEqualTo("id",startPage-20)
                        .findAllSorted("id",Sort.DESCENDING);
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
                adapterData.removeAll(realmData);
                adapterData.addAll(realmData);
                mCommonItemAdapter.updateItem(adapterData);
                swipeRefreshLayout.setRefreshing(false);
                endItemID=realmData.get(realmData.size()-1).getId();
                isLoadMore=false;
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    return RealmUtils.isOldImgDataFromRealm(params[0], getActivity());
                } else {
                    return -1;
                }
            }
        };

        if (isNew) {
            loadNewDatafromRealmTask.execute(startPage);
        } else {
            loadOldDatafromRealmTask.execute(startPage);
        }

    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mCommonItemAdapter.getItemCount()) {
                loadMore();//载入更多数据
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        }
    };


}
