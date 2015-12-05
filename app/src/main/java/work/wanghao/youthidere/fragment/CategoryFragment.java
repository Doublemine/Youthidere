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

import io.realm.Realm;
import work.wanghao.youthidere.CategoryPageAdapter;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.db.TabCategoriesHelper;
import work.wanghao.youthidere.model.Category;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.RealmUtils;

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
//    private List<Category> list;
    private List<CommonCategoryFragment> listFragment;
    private CategoryPageAdapter mAdapter;
    private Realm mRealm;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm= TabCategoriesHelper.getRealm(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter=new CategoryPageAdapter(getFragmentManager());
        initCategory();
       
    }
    
    private void initCategory(){
        AsyncTask<Void,Void,Integer> getCategoryFromRealm=new AsyncTask<Void, Void,Integer>() {
            @Override
            protected void onPostExecute(Integer categories) {
                List<Category> item=mRealm.where(Category.class)
                        .findAll();
                if(item.isEmpty()||item.size()==0){
                    return;
                }
                listFragment=new ArrayList<CommonCategoryFragment>();
                for(int i=0;i<item.size();i++){
                    listFragment.add(new CommonCategoryFragment(item.get(i)));
                    Log.e("listFragment",String.valueOf(item.size()));
                }

                mAdapter.setData(listFragment);
                mAdapter.setTitles(item);
                mViewPager.setAdapter(mAdapter);
                mTabLayout.setupWithViewPager(mViewPager);
                mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
            @Override
            protected Integer doInBackground(Void... params) {
                if(NetUtils.isNetConnect()){
                    return RealmUtils.isCategoriesDataFromRealm(getActivity());
                }else {
                    return -1;
                }
            }
        };
        getCategoryFromRealm.execute();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealm.close();
    }
}
