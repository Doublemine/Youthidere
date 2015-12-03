package work.wanghao.youthidere.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.dao.TokenDaoImpl;
import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.utils.DbUtils;

/**
 * Created by wangh on 2015-11-29-0029.
 */
public class AccountInfoFragment extends Fragment {
    private View mFragmentView;
    private CircleImageView userImage;
    private TextView userName;
    private TextView userEmail;
    private TextView userCreateAt;
    private Token mCurrentToken;
    private Button mBtnLogout;
    private AccountInfoFragmentCallback mCallBack;

    private static AccountInfoFragment accountInfoFragment;
    public static AccountInfoFragment getInstance(){
        if(accountInfoFragment==null){
            accountInfoFragment=new AccountInfoFragment();
        }
        return accountInfoFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.ActivityTheme_Login);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        mFragmentView = localInflater.inflate(R.layout.activity_account_info, container, false);
        mFragmentView.setBackgroundResource(R.color.primary);
        setAlreadyLoginHeader();
        return mFragmentView;
    }


    public interface AccountInfoFragmentCallback{
        void setMainActivityFlag(int flag);
        void setHeaderUserName(String name);
        void setHeaderUserEmail(String email);
        void setHeaderUserImageByUrl(String url);
        void setHeaderUserDefaultImage(int id);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userImage= (CircleImageView) mFragmentView.findViewById(R.id.activity_account_info_image);
        userName= (TextView) mFragmentView.findViewById(R.id.activity_account_info_username);
        userEmail= (TextView) mFragmentView.findViewById(R.id.activity_account_info_email);
        userCreateAt= (TextView) mFragmentView.findViewById(R.id.activity_account_info_create_at);
        mBtnLogout= (Button) mFragmentView.findViewById(R.id.activity_account_info_btn_logout);
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        mCurrentToken = DbUtils.getCurrentLoginUserToken(getActivity());
        Glide.with(this).load(mCurrentToken.getUser().getAvatar_url()).into(userImage);
        userName.setText(mCurrentToken.getUser().getName());
        userEmail.setText(mCurrentToken.getUser().getEmail());
        userCreateAt.setText(mCurrentToken.getUser().getCreated_at());
    }

    
    private void logout(){
        
        AsyncTask<Void,Void,Void> logoutTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                mBtnLogout.setEnabled(false);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
                mBtnLogout.setEnabled(true);
                mCallBack.setMainActivityFlag(R.layout.activity_login);
                mCallBack.setHeaderUserEmail(getString(R.string.nav_header_default_email_text));
                mCallBack.setHeaderUserName(getString(R.string.nav_header_default_username_text));
                mCallBack.setHeaderUserDefaultImage(R.drawable.xiamo);
                if(LoginFragment.getInstance().isAdded()){
                    getActivity().getSupportFragmentManager().beginTransaction().remove(AccountInfoFragment.getInstance()).show(LoginFragment.getInstance()).commit();
                }else {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(AccountInfoFragment.getInstance()).add(R.id.main_content,LoginFragment.getInstance()).commit();
                }
               
                
            }

            @Override
            protected Void doInBackground(Void... params) {
                TokenDaoImpl.getInstance(getActivity()).deleteAllToken();
                return null;
            }
        };
        
        logoutTask.execute();
    }
    
    
    private void setAlreadyLoginHeader(){
       Token token= DbUtils.getCurrentLoginUserToken(getActivity());
        mCallBack.setHeaderUserName(token.getUser().getName());
        mCallBack.setHeaderUserEmail(token.getUser().getEmail());
        mCallBack.setHeaderUserImageByUrl(token.getUser().getAvatar_url());
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity=getActivity();
        try{
            mCallBack= (AccountInfoFragmentCallback) activity;
        }catch (ClassCastException e){
            throw new ClassCastException(activity.toString()+ 
                    " must implement AccountInfoFragmentCallback");
        }
    }
}
