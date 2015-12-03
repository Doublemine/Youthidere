package work.wanghao.youthidere.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.RecyclerViewImgAdapter;

/**
 * Created by wangh on 2015-11-26-0026.
 * 为主页视频选项卡Fragment
 */
public class BrowserVedioFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private RecyclerViewImgAdapter mAdapter;
    private View mView;
   

    private static BrowserVedioFragment mBrowserVedioFragment;
    public static BrowserVedioFragment getInstance(){
        if(mBrowserVedioFragment==null){
            mBrowserVedioFragment=new BrowserVedioFragment();
        }
        return mBrowserVedioFragment;
    }
    
    
    public BrowserVedioFragment(){
       
       
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
         mView = inflater.inflate(R.layout.fragment_browser_vedio, container, false);
       
        return mView;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter=new RecyclerViewImgAdapter(getActivity(),BrowserImageFragment.items);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.fragment_browser_vedio_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
    }
}
