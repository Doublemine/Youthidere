package work.wanghao.youthidere.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import work.wanghao.youthidere.fragment.AccountCommentsFragment;
import work.wanghao.youthidere.fragment.AccountFavoriteFragment;
import work.wanghao.youthidere.fragment.ReadHistoryFragment;


/**
 * Created by wangh on 2015-11-23-0023.
 */
public class AccountFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[]{"阅读历史","收藏","点评"};
    private Context context;

    public AccountFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return   ReadHistoryFragment.getInstance();
        }else if(position==1){
            return AccountFavoriteFragment.getInstance();
        }else if(position==2){
            return AccountCommentsFragment.getInstance();
        }else {
            return null;
        }
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
