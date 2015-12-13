package work.wanghao.youthidere.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.AccountCommonAdapter;
import work.wanghao.youthidere.dao.TokenDaoImpl;
import work.wanghao.youthidere.model.ReadHistoryItem;
import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.utils.DateUtils;
import work.wanghao.youthidere.utils.HttpUtils;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.SQLiteUtils;

/**
 * Created by wangh on 2015-12-12-0012.
 */
public class ReadHistoryFragment extends Fragment implements DatePickerDialog.OnDateSetListener,SwipeRefreshLayout.OnRefreshListener {


    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mFabChooseDate;
    private FloatingActionButton mFabReutnTop;
    private FloatingActionButton mFabLogout;
    private SwipeRefreshLayout mRefresh;
    private RecyclerView mRecycler;
    private String mCurrentChooseDate;
    private View mView;
    private AccountCommonAdapter adapter;
    private List<ReadHistoryItem> mData;
    private Token token;
    private AccountInfoFragmentCallback mCallBack;
    private static ReadHistoryFragment  mFragment;
    
    public  static ReadHistoryFragment getInstance(){
        if(mFragment==null){
            mFragment=new ReadHistoryFragment();
        }
        return mFragment;
    }
    
    
    

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        mView = inflater.inflate(R.layout.fragment_read_history, container, false);
        mFabMenu = (FloatingActionMenu) mView.findViewById(R.id.fab_menu);
        mFabChooseDate = (FloatingActionButton) mView.findViewById(R.id.fab_menu_choose_date);
        mFabReutnTop = (FloatingActionButton) mView.findViewById(R.id.fab_return_top);
        mFabLogout = (FloatingActionButton) mView.findViewById(R.id.fab_menu_logout);
        mRecycler = (RecyclerView) mView.findViewById(R.id.frg_read_history_rv);
        mRefresh = (SwipeRefreshLayout) mView.findViewById(R.id.frg_read_history_srfl);
        mRefresh.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        return mView;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter=new AccountCommonAdapter(getActivity());
        mData=new ArrayList<ReadHistoryItem>();
        token= SQLiteUtils.getCurrentLoginUserToken(getActivity());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRefresh.setOnRefreshListener(this);
        mFabChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        ReadHistoryFragment.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setTitle("选择一个日期来查看当天的阅读记录");
                dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
                mFabMenu.close(true);
            }
        });
        mFabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                logout();
            }
        });
        mFabReutnTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                mRecycler.scrollToPosition(0);
            }
        });
        mRecycler.setAdapter(adapter);
        initData();

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mCurrentChooseDate = year + "-" + (++monthOfYear) + "-" + dayOfMonth;
        Log.e("当前日期", mCurrentChooseDate);
        getDataByDate(token, mCurrentChooseDate);
    }

    @Override
    public void onRefresh() {
        getDataByDate(token,mCurrentChooseDate);
    }
    
    private void initData(){
        getDataByDate(token, DateUtils.getSystemDateByDay());
    }
    
    private void getDataByDate(Token token,String strDate){
        String strToken=token.getToken();
        AsyncTask<String,Void,List<ReadHistoryItem>> initTask=new AsyncTask<String, Void, List<ReadHistoryItem>>() {
            @Override
            protected void onPostExecute(List<ReadHistoryItem> readHistoryItems) {
                if(readHistoryItems==null){
                    Snackbar.make(mView,"发生网络错误无法获取阅读历史数据!",Snackbar.LENGTH_SHORT).show();
                    mRefresh.setRefreshing(false);
                    return;
                }else {
                    if(readHistoryItems.size()<=0){
                        Snackbar.make(mView,"该天您没有阅读记录哦~",Snackbar.LENGTH_SHORT).show();
                    }
                    mData.clear();
                    mData.addAll(readHistoryItems);
                    adapter.updateItem(mData);
                    mRefresh.setRefreshing(false);
                }
            }

            @Override
            protected List<ReadHistoryItem> doInBackground(String... params) {
               if(NetUtils.isNetConnect()){
                   return HttpUtils.getReadHistoryFromServer(params[0],params[1]);
               }else {
                   return null;
               }
            }

            @Override
            protected void onPreExecute() {
                mRefresh.setRefreshing(true);
            }
        };
        initTask.execute(strToken, strDate);
    }




    private void logout(){

        AsyncTask<Void,Void,Void> logoutTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
//                mBtnLogout.setEnabled(false);
            }

            @Override
            protected void onPostExecute(Void aVoid) {
//                mBtnLogout.setEnabled(true);
                mCallBack.setMainActivityFlag(R.layout.activity_login);
                mCallBack.setHeaderUserEmail(getString(R.string.nav_header_default_email_text));
                mCallBack.setHeaderUserName(getString(R.string.nav_header_default_username_text));
                mCallBack.setHeaderUserDefaultImage(R.drawable.xiamo);
                if(LoginFragment.getInstance().isAdded()){
                    getActivity().getSupportFragmentManager().beginTransaction().remove(ReadHistoryFragment.getInstance()).remove(AccountCommentsFragment.getInstance()).remove(AccountCommentsFragment.getInstance()).remove(AccountContainerFragment.getInstance()).show(LoginFragment.getInstance()).commit();
                    
                }else {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(ReadHistoryFragment.getInstance()).remove(AccountCommentsFragment.getInstance()).remove(AccountCommentsFragment.getInstance()).remove(AccountContainerFragment.getInstance()).add(R.id.main_content, LoginFragment.getInstance()).commit();
//                    getActivity().getSupportFragmentManager().beginTransaction().remove(AccountContainerFragment.getInstance()).add(R.id.main_content,LoginFragment.getInstance()).commit();
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

    public interface AccountInfoFragmentCallback{
        void setMainActivityFlag(int flag);
        void setHeaderUserName(String name);
        void setHeaderUserEmail(String email);
        void setHeaderUserImageByUrl(String url);
        void setHeaderUserDefaultImage(int id);
        void setTitle(String title);
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
