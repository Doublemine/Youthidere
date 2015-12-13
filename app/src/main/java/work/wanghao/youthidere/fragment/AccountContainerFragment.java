package work.wanghao.youthidere.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.AccountFragmentPagerAdapter;
import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.utils.SQLiteUtils;

/**
 * Created by wangh on 2015-12-12-0012.
 */
public class AccountContainerFragment extends Fragment {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private View mView;
    private AccountFragmentPagerAdapter adapter;
    private CircleImageView userAvatar;
    private TextView userName;
    private TextView userEmail;
    private Token token;
    private static AccountContainerFragment accountFragment;
    
    public static AccountContainerFragment getInstance(){
        if(accountFragment==null){
            accountFragment=new AccountContainerFragment();
        }
        return accountFragment;
    }
    
    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        mView=inflater.inflate(R.layout.fragment_account_tab_bottom,container,false);
        mViewPager= (ViewPager) mView.findViewById(R.id.fragment_vp);
        mTabLayout= (TabLayout) mView.findViewById(R.id.fragment_tab_layout);
        userAvatar= (CircleImageView) mView.findViewById(R.id.activity_account_info_image);
        userName= (TextView) mView.findViewById(R.id.activity_account_info_username);
        userEmail= (TextView) mView.findViewById(R.id.activity_account_info_email);
        return mView;

    }

    private void  initData(){
        
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter=new AccountFragmentPagerAdapter(getActivity().getSupportFragmentManager(),getContext());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        token= SQLiteUtils.getCurrentLoginUserToken(getActivity());
        Glide.with(this).load(token.getUser().getAvatar_url()).into(userAvatar);
        userName.setText(token.getUser().getName());
        userEmail.setText(token.getUser().getEmail());
    }

   
}
