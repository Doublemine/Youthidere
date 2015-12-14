package work.wanghao.youthidere.fragment;

import android.annotation.SuppressLint;
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

import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.CommonItemAdapter;
import work.wanghao.youthidere.db.PostItemRealmHelper;
import work.wanghao.youthidere.model.Category;
import work.wanghao.youthidere.model.PostItem;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.RealmUtils;

/**
 * Created by wangh on 2015-12-2-0002.
 */
public class CommonCategoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    private View mView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private CommonItemAdapter mAdapter;
    private List<PostItem> mData= new ArrayList<PostItem>();;
    private int lastVisibleItem;
    private int firstItemID;
    private int endItemID;
    private Category category;
    private boolean isLoadMore;
    private Realm mRealm;
    private boolean isInit;
    private  String strOfCategory;
    private FloatingActionButton mFab;
    private int mScrollOffset=4;
    @SuppressLint("ValidFragment")
    public CommonCategoryFragment(Category category) {
        this.category=category;
        strOfCategory=category.getSlug();
    }
    @SuppressLint("ValidFragment")
    public CommonCategoryFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInit=true;
        mRealm= PostItemRealmHelper.getRealm(getActivity());
        mAdapter=new CommonItemAdapter(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView=inflater.inflate(R.layout.fragment_browser_image,container,false);
        mFab= (FloatingActionButton) mView.findViewById(R.id.fab_menu_return_top);
        swipeRefreshLayout= (SwipeRefreshLayout) mView.findViewById(R.id.frg_browser_image_srfl);
        recyclerView= (RecyclerView) mView.findViewById(R.id.fragment_browser_image_rv);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnScrollListener(mOnScrollListener);
//        ScaleInAnimationAdapter scaleInAnimationAdapter=new ScaleInAnimationAdapter(mAdapter);
//        scaleInAnimationAdapter.setFirstOnly(false);
//        scaleInAnimationAdapter.setDuration(800);
        recyclerView.setAdapter(mAdapter);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);
       
        initAdapterData();
    }

    /**
     * 
     * @param isVisibleToUser
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){//当前Fragment正在被展示
            
        }else {
            //页面没有展示
            
        }
    }

   
    @Override
    public void onRefresh() {
        loadData(true, firstItemID);
    }

    private void initAdapterData() {
        if (isInit) {//载入cache
            Number number = mRealm.where(PostItem.class).equalTo("category_slug",strOfCategory).max("id");
            if (number == null) {//本地没有缓存--->请求网络数据
                if (NetUtils.isNetConnect()) {
                    loadData(true,0);
                } else {
                    Snackbar.make(mView, "无网络连接，无法从服务器上获取数据，请检查网络!", Snackbar.LENGTH_INDEFINITE).show();
                }

            } else {//本地有缓存--->加载缓存

                List<PostItem> initData = mRealm.where(PostItem.class)
                        .equalTo("category_slug",strOfCategory)
                        .lessThanOrEqualTo("id", number.intValue())
                        .findAllSorted("id", Sort.DESCENDING);

                if (initData == null || initData.size() <= 0) {//没有数据---不太可能
                    if (NetUtils.isNetConnect()) {//有网络-->请求数据
                        Snackbar.make(mView, "你遇到了一个不常见的Bug，请访问：notes.wanghao.work反馈给作者吧:)", Snackbar.LENGTH_LONG).show();
                    } else {//无网络
                        Snackbar.make(mView, "你遇到了一个不常见的Bug，请访问：notes.wanghao.work反馈给作者吧:)", Snackbar.LENGTH_LONG).show();
                    }
                } else {//有数据
                    if(initData.size()>20){
                        initData=initData.subList(0,19);
                    }
                    mData.removeAll(initData);
                    mData.addAll(initData);
                    mAdapter.updateItem(mData);
                    endItemID = initData.get(initData.size() - 1).getId();
                    firstItemID = initData.get(0).getId();
                    isInit = false;
                    //到这里缓存加载完毕接下来 看有无网络进行更新数据
                    if (NetUtils.isNetConnect()) {//有网络--》更新数据
                        loadData(true,number.intValue());
                    } else {//无网络
                        Snackbar.make(mView, "无网络连接，先看看本地缓存吧!", Snackbar.LENGTH_INDEFINITE).show();
                        return;
                    }
                }
            }
        }
    }

    private void loadMore() {
        if(isLoadMore){
            return;
        }
        isLoadMore=true;
        loadData(false, endItemID);
        Log.e("载入更多数据", "endItemId=" + endItemID);
    }
    
    
    /**
     * 载入数据函数
     * @param isUpdate 是否是下拉刷新数据
     * @param startId 开始的item的id号
     */
    private void loadData(boolean isUpdate,  final int startId){
        AsyncTask<Integer, Void, Integer> loadNewDatafromRealmTask = new AsyncTask<Integer, Void, Integer>() {

            @Override
            protected void onPreExecute() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                List<PostItem> realmData=null;
               if(integer==-1){
                   swipeRefreshLayout.setRefreshing(false);
                   Snackbar.make(mView,"无网络连接，无法从服务器上获取数据，请检查网络!",Snackbar.LENGTH_SHORT).show();
                   return;
               }else {
                   if(startId==0){
                       Number maxId=mRealm.where(PostItem.class).equalTo("category_slug",strOfCategory).max("id");
                       if(maxId==null){
                           Snackbar.make(mView,"该分类的内容都被管理员移除了，请查看其它分类吧:)",Snackbar.LENGTH_SHORT).show();
                           return;
                       }
                       realmData=mRealm.where(PostItem.class)
                               .equalTo("category_slug",strOfCategory)
                               .lessThanOrEqualTo("id", maxId.intValue())
                               .findAllSorted("id",Sort.DESCENDING);
                   }else {
                       realmData=mRealm.where(PostItem.class)
                               .equalTo("category_slug",strOfCategory)
                               .greaterThan("id", startId)
                               .findAllSorted("id", Sort.DESCENDING);
                   }
                  
                   if (realmData==null || realmData.size() == 0) {
                       swipeRefreshLayout.setRefreshing(false);
                       Snackbar.make(mView,"已经是最新的数据咯~",Snackbar.LENGTH_SHORT).show();
                       return;
                   }
                  if(realmData.size()>20){
                      realmData=realmData.subList(0,19);
                  }
                   mData.removeAll(realmData);
                   mData.addAll(0, realmData);
                   mAdapter.updateItem(mData);
                   swipeRefreshLayout.setRefreshing(false);
                   endItemID=realmData.get(realmData.size()-1).getId();
                   firstItemID=realmData.get(0).getId();
               }
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    Log.e("网络连接", "网络已连接");
                    return RealmUtils.isNewPostItemByCategoryFromRealm(params[0], strOfCategory, getActivity());
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
                        .equalTo("category_slug",strOfCategory)
                        .lessThan("id",startId)
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
                
                if(realmData.size()>20){
                    realmData=realmData.subList(0,19);
                }
                mData.removeAll(realmData);
                mData.addAll(realmData);
                mAdapter.updateItem(mData);
                swipeRefreshLayout.setRefreshing(false);
                endItemID=realmData.get(realmData.size()-1).getId();
                isLoadMore=false;
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    return RealmUtils.isOldPostItemByCategoryFromRealm(params[0], strOfCategory, getActivity());
                } else {
                    return -1;
                }
            }
        };
        
        if(isUpdate){
            loadNewDatafromRealmTask.execute(startId);
        }else {
            Log.e("执行任务","--");
            loadOldDatafromRealmTask.execute(startId);
        }
    }
    /*
    Fragment对用户可见的时候调用
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                loadMore();//载入更多数据
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            if (Math.abs(dy) > mScrollOffset) {
                if (dy > 0) {
                    mFab.hide(true);
                } else {
                    mFab.show(true);
                }
            }
        }
    };
    
}
