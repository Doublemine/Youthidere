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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import work.wanghao.youthidere.R;
import work.wanghao.youthidere.utils.HttpUtils;

/**
 * Created by wangh on 2015-11-28-0028.
 */
public class LoginFragment extends Fragment {
    //    private final Context mContext;
    private LayoutInflater mLayoutInflater;
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private TextView signupLink;
    private View mFragmentView;
    private LoginCallback mCallback;


    private static LoginFragment mLoginFragment;

    public static LoginFragment getInstance() {
        if (mLoginFragment == null) {
            mLoginFragment = new LoginFragment();
        }
        return mLoginFragment;
    }

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //设置Fragment的style
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.ActivityTheme_Login);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);

        mFragmentView = localInflater.inflate(R.layout.activity_login, container, false);
        mFragmentView.setBackgroundResource(R.color.primary);
        emailText = (EditText) mFragmentView.findViewById(R.id.activity_login_input_email);
        passwordText = (EditText) mFragmentView.findViewById(R.id.activity_login_input_password);
        loginButton = (Button) mFragmentView.findViewById(R.id.activity_login_btn_login);
        signupLink = (TextView) mFragmentView.findViewById(R.id.activity_login_link_signup);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 跳转到注册Fragment
                if (SignupFragment.getInstance().isAdded()) {
                    getActivity().getSupportFragmentManager().beginTransaction().hide(LoginFragment.getInstance()).show(SignupFragment.getInstance()).commit();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().hide(LoginFragment.getInstance()).add(R.id.main_content, SignupFragment.getInstance()).commit();
                }

            }
        });

        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().hide();
        }
        return mFragmentView;
    }

    public void login() {
        Log.d("TAG", "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(getActivity(),
                R.style.ActivityTheme_Dark_Dialog);
//        progressDialog.setCanceledOnTouchOutside(false);//按其他地方不起作用，按返回键起作用
        progressDialog.setCancelable(false);//点击屏幕其他地方都不起作用
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.actvity_login_string_authenticate));


        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        AsyncTask<String, Void, Integer> loginTask = new AsyncTask<String, Void, Integer>() {
            @Override
            protected void onPostExecute(Integer aVoid) {


                if (aVoid == -1) {//登录失败
                    progressDialog.setMessage("账号或者密码错误！");

                    Snackbar.make(mFragmentView, "账号或者密码错误！", Snackbar.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                    progressDialog.dismiss();
                } else if (aVoid == 0) {//登录成功
                    progressDialog.setMessage("登录成功!");
                    Snackbar.make(mFragmentView, "登录成功!", Snackbar.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    if (AccountInfoFragment.getInstance().isAdded()) {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(LoginFragment.getInstance()).show(AccountContainerFragment.getInstance()).commit();
                        mCallback.setMainActivityFlag(R.layout.activity_account_info);
                    } else {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(LoginFragment.getInstance()).add(R.id.main_content, AccountContainerFragment.getInstance()).commit();
                        mCallback.setMainActivityFlag(R.layout.activity_account_info);
                    }
//                  


                } else if (aVoid == 1) {//网络错误
                    progressDialog.setMessage("网络错误导致登录失败！");
                    progressDialog.dismiss();
                    Snackbar.make(mFragmentView, "网络错误导致登录失败！", Snackbar.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                } else if (aVoid == 2) {//数据库写入错误
                    progressDialog.setMessage("数据库写入错误导致登录失败！");
                    Snackbar.make(mFragmentView, "数据库写入错误导致登录失败！", Snackbar.LENGTH_SHORT).show();
                    loginButton.setEnabled(true);
                    progressDialog.dismiss();
                }
            }

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected Integer doInBackground(String... params) {
                return HttpUtils.httpLogin(params[0], params[1], getActivity());

            }
        };
        loginTask.execute(email, password);
    }


    public void onLoginSuccess() {
        loginButton.setEnabled(true);
    }

    public void onLoginFailed() {
        Snackbar.make(mFragmentView, "请输入正确的账号和密码再点击登录!", Snackbar.LENGTH_INDEFINITE).show();
    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(getString(R.string.actvity_login_string_email_adress_input_error));
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(getString(R.string.actvity_login_string_password_input_error));
            valid = false;
        } else {
            passwordText.setError(null);
        }
        return valid;
    }

//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mCallback= (LoginCallback) activity;
//        }catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//       
//    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        try {
            mCallback = (LoginCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface LoginCallback {
        void setMainActivityFlag(int flag);
        void setTitle(String title);
    }
}

