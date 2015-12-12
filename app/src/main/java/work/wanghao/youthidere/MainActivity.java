package work.wanghao.youthidere;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import work.wanghao.youthidere.activity.SettingsActivity;
import work.wanghao.youthidere.fragment.AboutFragment;
import work.wanghao.youthidere.fragment.AccountInfoFragment;
import work.wanghao.youthidere.fragment.CategoryFragment;
import work.wanghao.youthidere.fragment.HomeFragment;
import work.wanghao.youthidere.fragment.LoginFragment;
import work.wanghao.youthidere.fragment.SignupFragment;
import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.SQLiteUtils;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
        ,LoginFragment.LoginCallback,SignupFragment.SignUpCallBack
        ,AccountInfoFragment.AccountInfoFragmentCallback {
    
    private DrawerLayout drawerLayout;
    private Toolbar mToolbar;
    private FragmentManager mFragmentMgr;

    private Fragment mHomeFragment;
    private Fragment mAboutFragment;
    private Fragment mSubmitDataFragment;
    private Fragment mCategoryFragment;
    
    private Fragment mLoginFragment;


    private int BEFORE_FRAGMENT_FLAG;//之前的fragment标志位
    private int ACCOUNT_FLAG;
    

    private NavigationView navigationView;
    private CircleImageView userHeadImage;
    private TextView userName;
    private TextView userEmail;
    private Token isLoginToken;
    
    private void initView() {

        navigationView= (NavigationView) findViewById(R.id.nav_view);
        View headView=navigationView.getHeaderView(0);
        userHeadImage= (CircleImageView) headView.findViewById(R.id.nav_header_user_image);
        userName= (TextView) headView.findViewById(R.id.nav_header_username);
        userEmail= (TextView) headView.findViewById(R.id.nav_header_email);
        isLoginToken= SQLiteUtils.getCurrentLoginUserToken(this);
        if(isLoginToken!=null){
            ACCOUNT_FLAG=R.layout.activity_account_info;//设置账号列表标志位
            userName.setText(isLoginToken.getUser().getName());
            userEmail.setText(isLoginToken.getUser().getEmail());
            Glide.with(this).load(isLoginToken.getUser().getAvatar_url()).into(userHeadImage);
        }else {
            ACCOUNT_FLAG=R.layout.activity_login;
        }

        /**
         * TODO: 2015-11-26-0026  
         * 此处Fragment待优化==》单例模式
         * 以及屏幕旋转的bug
         */

        mHomeFragment = HomeFragment.getInstance();
//        mSubmitDataFragment = SubmitDataFragment.getInstance();
        mAboutFragment = AboutFragment.getInstance();
        mCategoryFragment = CategoryFragment.getInstance();
        mLoginFragment=LoginFragment.getInstance();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        NetUtils.setConnectivityManager((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE));
        initView();


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

       
        navigationView.setNavigationItemSelectedListener(this);

        mFragmentMgr = getSupportFragmentManager();
        mFragmentMgr.beginTransaction().add(R.id.main_content, mHomeFragment, "homeFragment").commit();
        BEFORE_FRAGMENT_FLAG = R.id.nav_home;

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("activity已经结束","Over");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {

            switchFragment(mHomeFragment, "HomeFragment");
            BEFORE_FRAGMENT_FLAG = R.id.nav_home;
//            addFragmentList(mHomeFragment);

        } else if (id == R.id.nav_category) {

            switchFragment(mCategoryFragment, "CategoryFragment");
            BEFORE_FRAGMENT_FLAG = R.id.nav_category;
//            addFragmentList(mAboutFragment);

        } 
//        else if (id == R.id.nav_submit_for_publication) {
//
//            switchFragment(mSubmitDataFragment, "SubmitDataFragment");
//            BEFORE_FRAGMENT_FLAG = R.id.nav_submit_for_publication;
////            addFragmentList(mSubmitDataFragment);
//        } 
        else if (id == R.id.nav_account) {

            if (ACCOUNT_FLAG == R.layout.activity_login) {
                switchFragment(LoginFragment.getInstance(), "LoginFragment");
            } else if (ACCOUNT_FLAG == R.layout.activity_signup) {
                switchFragment(SignupFragment.getInstance(), "LoginFragment");
            } else if (ACCOUNT_FLAG == R.layout.activity_account_info) {
                switchFragment(AccountInfoFragment.getInstance(), "LoginFragment");
            }
            BEFORE_FRAGMENT_FLAG = R.id.nav_account;

        } else if (id == R.id.nav_settings) {
//            Toast.makeText(this,new WebView(MainActivity.this).getSettingstring(),Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_about) {

            switchFragment(mAboutFragment, "AboutFragment");
            BEFORE_FRAGMENT_FLAG = R.id.nav_about;
//            addFragmentList(mAboutFragment);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void switchFragment(Fragment to, String tag) {
        if (!to.isAdded()) {
            switch (BEFORE_FRAGMENT_FLAG) {
                case R.id.nav_home:
                    mFragmentMgr.beginTransaction().hide(mHomeFragment).add(R.id.main_content, to, tag).commit();
                    break;
                case R.id.nav_category:
                    mFragmentMgr.beginTransaction().hide(mCategoryFragment).add(R.id.main_content, to, tag).commit();
                    break;
//                case R.id.nav_submit_for_publication:
//                    mFragmentMgr.beginTransaction().hide(mSubmitDataFragment).add(R.id.main_content, to, tag).commit();
//                    break;
                case R.id.nav_account:
                    if(ACCOUNT_FLAG==R.layout.activity_login)
                    {
                        mFragmentMgr.beginTransaction().hide(mLoginFragment).add(R.id.main_content, to, tag).commit();
                    }else if(ACCOUNT_FLAG==R.layout.activity_signup){
                        mFragmentMgr.beginTransaction().hide(SignupFragment.getInstance()).add(R.id.main_content, to, tag).commit(); 
                    }else if(ACCOUNT_FLAG==R.layout.activity_account_info){
                        mFragmentMgr.beginTransaction().hide(AccountInfoFragment.getInstance()).add(R.id.main_content, to, tag).commit();
                    }
                   
                    break;
                case R.id.nav_about:
                    mFragmentMgr.beginTransaction().hide(mAboutFragment).add(R.id.main_content, to, tag).commit();
                    break;
            }

        } else {
            switch (BEFORE_FRAGMENT_FLAG) {
                case R.id.nav_home:
                    mFragmentMgr.beginTransaction().hide(mHomeFragment).show(to).commit();
                    break;
                case R.id.nav_category:
                    mFragmentMgr.beginTransaction().hide(mCategoryFragment).show(to).commit();
                    break;
//                case R.id.nav_submit_for_publication:
//                    mFragmentMgr.beginTransaction().hide(mSubmitDataFragment).show(to).commit();
//                    break;
                case R.id.nav_account:
                    if(ACCOUNT_FLAG==R.layout.activity_login)
                    {
                        mFragmentMgr.beginTransaction().hide(LoginFragment.getInstance()).show(to).commit();
                    }else if(ACCOUNT_FLAG==R.layout.activity_signup){
                        mFragmentMgr.beginTransaction().hide(SignupFragment.getInstance()).show(to).commit();
                    }else if(ACCOUNT_FLAG==R.layout.activity_account_info){
                        mFragmentMgr.beginTransaction().hide(AccountInfoFragment.getInstance()).show(to).commit();
                    }
                   
                 case R.id.nav_about:
                    mFragmentMgr.beginTransaction().hide(mAboutFragment).show(to).commit();
                    break;
            }
        }
    }


    @Override
    public void setMainActivityFlag(int flag) {
        ACCOUNT_FLAG=flag;
    }

    @Override
    public void setHeaderUserName(String name) {
        userName.setText(name);
    }

    @Override
    public void setHeaderUserEmail(String email) {
        userEmail.setText(email);
    }

    @Override
    public void setHeaderUserImageByUrl(String url) {
        Glide.with(this).load(url).into(userHeadImage);
    }

    @Override
    public void setHeaderUserDefaultImage(int id) {
        userHeadImage.setImageResource(id);

    }
}

