package work.wanghao.youthidere.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.SimpleFragmentPagerAdapter;

/**
 * Created by wangh on 2015-11-25-0025.
 * 
 * 为主页三个Fragment的容器Fragment
 */
public class HomeFragment extends Fragment {

    private View mFragment;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private SimpleFragmentPagerAdapter simpleFragmentPagerAdapter;
    private PagerAdapter mPagerAdapter;
    private static HomeFragment homeFragment;
    public  static HomeFragment getInstance(){
        if(homeFragment==null){
            homeFragment=new HomeFragment();
        }
        return homeFragment;
    }

    public HomeFragment(){
    }

    private void initView(){
        mTabLayout= (TabLayout) mFragment.findViewById(R.id.fragment_main_tabs);
        mViewPager= (ViewPager) mFragment.findViewById(R.id.fragment_main_view_pager);

        simpleFragmentPagerAdapter =new SimpleFragmentPagerAdapter(getActivity().getSupportFragmentManager(),getContext());
        mViewPager.setAdapter(simpleFragmentPagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mFragment=inflater.inflate(R.layout.fragment_main,container,false);
        initView();
        Log.e("目录为", getActivity().getExternalCacheDir().toString());
        return mFragment;
    }


    public interface OnFragmentMethodListener{

    }
}
