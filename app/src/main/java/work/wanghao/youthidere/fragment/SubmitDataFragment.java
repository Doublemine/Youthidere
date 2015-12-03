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
 * 为投稿界面Fragment
 */
public class SubmitDataFragment extends Fragment {
    
    private View mFragmentView;
    private static SubmitDataFragment mSubmitDataFragment;
    
    public static SubmitDataFragment getInstance(){
        if(mSubmitDataFragment==null){
            mSubmitDataFragment=new SubmitDataFragment();
        }
        return mSubmitDataFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mFragmentView=inflater.inflate(R.layout.fragment_submit_data,container,false);
        return mFragmentView;
    }
    
}
