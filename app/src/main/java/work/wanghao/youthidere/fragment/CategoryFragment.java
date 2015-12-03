package work.wanghao.youthidere.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import work.wanghao.youthidere.CategoryPageAdapter;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.model.Category;
import work.wanghao.youthidere.utils.HttpUtils;

/**
 * Created by wangh on 2015-11-25-0025.
 * 
 * 为类别主Fragment
 */
public class CategoryFragment extends Fragment{
    private static CategoryFragment mCategoryFragment;
    public static  CategoryFragment getInstance(){
        if(mCategoryFragment==null){
            mCategoryFragment=new CategoryFragment();
        }
        return mCategoryFragment;
    }

    private View mView;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Category> list;
    private List<CommonCategoryFragment> listFragment;
    private CategoryPageAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mView=inflater.inflate(R.layout.fragment_category,container,false);
        mTabLayout= (TabLayout) mView.findViewById(R.id.fragment_category_tabs);
        mViewPager= (ViewPager) mView.findViewById(R.id.fragment_category_view_pager);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter=new CategoryPageAdapter(getFragmentManager());
        initCategory();
       
    }
    
    private void initCategory(){
        AsyncTask<Void,Void,List<Category>> getCategory=new AsyncTask<Void, Void, List<Category>>() {
            @Override
            protected void onPostExecute(List<Category> categories) {
                if(categories==null){
                    return;
                }else {
                    list=categories;
                    Log.e("category",String.valueOf(categories.size()));
                    Log.e("list为空？",String.valueOf(list.isEmpty()));
                    listFragment=new ArrayList<CommonCategoryFragment>();
                    for(int i=0;i<categories.size();i++){
                        listFragment.add(new CommonCategoryFragment(list.get(i)));
                        Log.e("listFragment",String.valueOf(list.size()));
                    }
                    
                    mAdapter.setData(listFragment);
                    mAdapter.setTitles(list);
                    mViewPager.setAdapter(mAdapter);
                    mTabLayout.setupWithViewPager(mViewPager);
                    mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                }
            }

            @Override
            protected void onPreExecute() {
                list=new ArrayList<Category>();
            }

            @Override
            protected List<Category> doInBackground(Void... params) {
                return HttpUtils.getCategoriesDataFromServer();
            }
        };
        getCategory.execute();
    }
}
