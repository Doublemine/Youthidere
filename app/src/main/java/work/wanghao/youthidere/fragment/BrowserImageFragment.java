package work.wanghao.youthidere.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
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
import work.wanghao.youthidere.adapter.CommonItemAdapter;
import work.wanghao.youthidere.model.PostItem;
import work.wanghao.youthidere.utils.HttpUtils;

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

    private List<PostItem> adapterData = new ArrayList<PostItem>();


    public static BrowserImageFragment getInstance() {
        if (mBrowserImageFragment == null) {
            mBrowserImageFragment = new BrowserImageFragment();
        }
        return mBrowserImageFragment;
    }

    public static List<PostItem> items = new ArrayList<>();

    public BrowserImageFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCommonItemAdapter = new CommonItemAdapter(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_browser_image, container, false);
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
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.fragment_browser_image_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mRecyclerView.setAdapter(mCommonItemAdapter);
        initAdapterData();
    }

    @Override
    public void onRefresh() {
        reloadData();
    }

    private void reloadData() {
        swipeRefreshLayout.setRefreshing(true);
        loadData(firstItemID, true);
    }


    private void loadMore() {
        if (isLoadMore) {
            return;
        }

        isLoadMore = true;
        loadData(endItemID, false);
    }


    private void initAdapterData() {
        swipeRefreshLayout.setRefreshing(true);
        loadData(0,true);
    }

    private void loadData(int startPage, boolean isNew) {
        Log.e("开始载入数据", "--------------------->" + "startPage=" + startPage + "isNew=" + String.valueOf(isNew));
        AsyncTask<Integer, Void, List<PostItem>> updateTask = new AsyncTask<Integer, Void, List<PostItem>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<PostItem> items) {
                if (items == null) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                } else {
                    if (items.size() == 0) {
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    adapterData.addAll(items);
                    mCommonItemAdapter.updateItem(adapterData);
                    swipeRefreshLayout.setRefreshing(false);
                    endItemID = items.get(items.size() - 1).getId();
                    firstItemID = items.get(0).getId();
                    Log.e("updateTask数据载入完毕", "--------------------->" + "lastVisibleItem=" + String.valueOf(swipeRefreshLayout.isRefreshing()));

                }
            }

            @Override
            protected List<PostItem> doInBackground(Integer... params) {
                return HttpUtils.getNewImgDataFromServer(params[0]);

            }
        };

        AsyncTask<Integer, Void, List<PostItem>> updateOldTask = new AsyncTask<Integer, Void, List<PostItem>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(List<PostItem> items) {
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
                    adapterData.addAll(items);
                    mCommonItemAdapter.updateItem(adapterData);
                    swipeRefreshLayout.setRefreshing(false);
                    isLoadMore = false;
                    endItemID = items.get(items.size() - 1).getId();
                    firstItemID = items.get(0).getId();
                    Log.e("updateOldTask数据载入完毕", "--------------------->" + "swipeRefreshLayout=" + String.valueOf(swipeRefreshLayout.isRefreshing()));

                }
//                Log.e("updateOldTask数据载入完毕", "--------------------->" + "swipeRefreshLayout=" + String.valueOf(swipeRefreshLayout.isRefreshing()));
            }

            @Override
            protected List<PostItem> doInBackground(Integer... params) {
                return HttpUtils.getOldImgDataFromServer(params[0]);

            }
        };

        if (isNew) {
            updateTask.execute(startPage);
        } else {
            updateOldTask.execute(startPage);
        }

    }


    private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mCommonItemAdapter.getItemCount()) {
                swipeRefreshLayout.setRefreshing(true);
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
