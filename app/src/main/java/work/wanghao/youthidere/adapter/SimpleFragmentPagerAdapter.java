package work.wanghao.youthidere.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import work.wanghao.youthidere.fragment.BrowserImageFragment;
import work.wanghao.youthidere.fragment.ExploreFragment;


/**
 * Created by wangh on 2015-11-23-0023.
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"图摘","发现"};
    private Context context;

    public SimpleFragmentPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return  BrowserImageFragment.getInstance();
        }else if(position==1){
            return   ExploreFragment.getInstance();
        }else {
            return null;
        }
//        return TabFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
