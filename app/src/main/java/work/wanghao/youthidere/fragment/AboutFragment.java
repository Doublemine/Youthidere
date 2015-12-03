package work.wanghao.youthidere.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import work.wanghao.youthidere.R;

/**
 * Created by wangh on 2015-11-25-0025.
 * 
 * 为主页上关于Fragment
 */
public class AboutFragment extends Fragment {
    
    private View mFragmentView;
    private static AboutFragment mAboutFragment;

    public static AboutFragment getInstance(){
        if(mAboutFragment==null){
            mAboutFragment=new AboutFragment();
        }
        return mAboutFragment;
    }
    
    public AboutFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
//        return super.onCreateView(inflater, container, savedInstanceState);
        mFragmentView=inflater.inflate(R.layout.fragment_about,container,false);
        return  mFragmentView;
    }
}
