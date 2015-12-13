package work.wanghao.youthidere.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import work.wanghao.youthidere.fragment.CommonCategoryFragment;
import work.wanghao.youthidere.model.Category;

/**
 * Created by wangh on 2015-12-2-0002.
 */
public class CategoryPageAdapter extends FragmentPagerAdapter {
    
    private List<Category> titles;
    private List<CommonCategoryFragment> mFragmentdatas;
    public CategoryPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setData(List<CommonCategoryFragment> datas) {
        this.mFragmentdatas = datas;
    }

    public void setTitles(List<Category> titles) {
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentdatas.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentdatas.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position).getName();
    }
}
