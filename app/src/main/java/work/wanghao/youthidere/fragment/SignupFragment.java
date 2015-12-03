package work.wanghao.youthidere.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import work.wanghao.youthidere.R;
import work.wanghao.youthidere.utils.HttpUtils;

/**
 * Created by wangh on 2015-11-28-0028.
 */
public class SignupFragment extends Fragment {


    private EditText nameText;
    private EditText editText;
    private EditText passwordText;
    private Button signupButton;
    private TextView loginLink;
    private View mRegisterFragment;
    private SignUpCallBack mCallback;
    
    private static SignupFragment mSignupFragment;
    public  static SignupFragment getInstance(){
        if(mSignupFragment==null){
            mSignupFragment=new SignupFragment();
        }
        return mSignupFragment;
    }

    public SignupFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.ActivityTheme_Login);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        mRegisterFragment = localInflater.inflate(R.layout.activity_signup, container, false);
        mRegisterFragment.setBackgroundResource(R.color.primary);
        nameText = (EditText) mRegisterFragment.findViewById(R.id.activity_signup_input_name);
        editText = (EditText) mRegisterFragment.findViewById(R.id.activity_signup_input_email);
        passwordText = (EditText) mRegisterFragment.findViewById(R.id.activity_signup_input_password);
        signupButton = (Button) mRegisterFragment.findViewById(R.id.activity_signup_btn_signup);
        loginLink = (TextView) mRegisterFragment.findViewById(R.id.activity_signup_link_login);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LoginFragment.getInstance().isAdded()){
                    getActivity().getSupportFragmentManager().beginTransaction().hide(SignupFragment.getInstance()).show(LoginFragment.getInstance()).commit();
                }else {
                    getActivity().getSupportFragmentManager().beginTransaction().hide(SignupFragment.getInstance()).add(R.id.main_content,LoginFragment.getInstance()).commit();
                }
                
            }
        });
        return mRegisterFragment;


    }


    public void signup() {
        Log.d("TAG", "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

//        signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.ActivityTheme_Dark_Dialog);
        //        progressDialog.setCanceledOnTouchOutside(false);//按其他地方不起作用，按返回键起作用
        progressDialog.setCancelable(false);//点击屏幕其他地方都不起作用
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.actvity_signup_string_signuping_msg));
       

        String name = nameText.getText().toString();
        String email = editText.getText().toString();
        String password = passwordText.getText().toString();


        AsyncTask<String,Void,Integer> regsterTask=new AsyncTask<String, Void, Integer>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected void onPostExecute(Integer integer) {
//                super.onPostExecute(integer);
                if(integer==-1){
                    Snackbar.make(mRegisterFragment, "账号已被注册,请重新注册或者去登录~", Snackbar.LENGTH_INDEFINITE).setAction("现在就去登录", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(LoginFragment.getInstance().isAdded()){
                                getActivity().getSupportFragmentManager().beginTransaction().hide(SignupFragment.getInstance()).show(LoginFragment.getInstance()).commit();
                            }else {
                                getActivity().getSupportFragmentManager().beginTransaction().hide(SignupFragment.getInstance()).add(R.id.main_content,LoginFragment.getInstance()).commit();
                            }
                        }
                    }).show();
                    progressDialog.dismiss();
                }else if(integer==-2){
                    Snackbar.make(mRegisterFragment, "因为你的非法操作，服务器拒绝了你的注册请求！", Snackbar.LENGTH_INDEFINITE).show();
                    progressDialog.dismiss();
                }
                else if(integer==0){
                    Snackbar.make(mRegisterFragment, "注册成功~", Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    if(AccountInfoFragment.getInstance().isAdded()){
                        getActivity().getSupportFragmentManager().beginTransaction().remove(SignupFragment.getInstance()).show(AccountInfoFragment.getInstance()).commit();
                        mCallback.setMainActivityFlag(R.layout.activity_account_info);
                    }else {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(SignupFragment.getInstance()).add(R.id.main_content,AccountInfoFragment.getInstance()).commit();
                        mCallback.setMainActivityFlag(R.layout.activity_account_info);
                    }
                }
                else if(integer==1){
                    Snackbar.make(mRegisterFragment, "因为网络原因，注册失败~", Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else if(integer==2){
                    Snackbar.make(mRegisterFragment, "数据库写入失败，注册失败~", Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            protected Integer doInBackground(String... params) {
               return HttpUtils.httpRegister(params[0],params[1],params[2],getActivity(),params[3]);
            }
        };
        
        regsterTask.execute(name,email,password,new WebView(getActivity()).getSettings().getUserAgentString());
    }


    public void onSignupSuccess() {
        signupButton.setEnabled(true);

    }

    public void onSignupFailed() {
        Snackbar.make(mRegisterFragment, "请确认输入的用户名、账号和密码无误后再点击注册!", Snackbar.LENGTH_LONG).show();
        signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = nameText.getText().toString();
        String email = editText.getText().toString();
        String password = passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            nameText.setError(getString(R.string.actvity_signup_string_username_input_error));
            valid = false;
        } else {
            nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editText.setError(getString(R.string.actvity_signup_string_email_address_input_error));
            valid = false;
        } else {
            editText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(getString(R.string.actvity_signup_string_password_input_error));
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
public interface SignUpCallBack{
    void setMainActivityFlag(int flag);
}
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity=getActivity();
        try {
            mCallback= (SignUpCallBack) activity;
        }catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
