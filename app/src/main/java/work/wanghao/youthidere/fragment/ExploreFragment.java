package work.wanghao.youthidere.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import jp.wasabeef.recyclerview.adapters.SlideInBottomAnimationAdapter;
import pl.droidsonroids.gif.GifImageView;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.adapter.RecyclerExploreAdapter;
import work.wanghao.youthidere.config.GlobalConfig;
import work.wanghao.youthidere.db.ExploreItemRealmHelper;
import work.wanghao.youthidere.model.Explore;
import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.utils.HttpUtils;
import work.wanghao.youthidere.utils.NetUtils;
import work.wanghao.youthidere.utils.RealmUtils;
import work.wanghao.youthidere.utils.SQLiteUtils;

/**
 * Created by wangh on 2015-11-26-0026.
 */
public class ExploreFragment extends Fragment implements Handler.Callback, SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private View mView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerExploreAdapter mAdapter;
    private int lastVisibleItem;
    private boolean isLoadMore = false;
    private List<Explore> mdata;
    private int firstItemID;
    private int endItemID;
    
    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mFabCamera;
    private FloatingActionButton mFabPhotoLib;

    private Realm mRealm;

    private boolean isInit;
    private static ExploreFragment mExploreFragment;

    public static ExploreFragment getInstance() {
        if (mExploreFragment == null) {
            mExploreFragment = new ExploreFragment();
        }
        return mExploreFragment;
    }

    public ExploreFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isInit=true;
        mAdapter = new RecyclerExploreAdapter(getActivity());
        mdata = new ArrayList<Explore>();
        mRealm = ExploreItemRealmHelper.getRealm(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_explore, container, false);
        mRecyclerView = (RecyclerView) mView.findViewById(R.id.fragment_explore_rv);
        mFabMenu= (FloatingActionMenu) mView.findViewById(R.id.fab_menu);
        mFabCamera= (FloatingActionButton) mView.findViewById(R.id.fab_menu_camera);
        mFabPhotoLib= (FloatingActionButton) mView.findViewById(R.id.fab_menu_photo);
        swipeRefreshLayout = (SwipeRefreshLayout) mView.findViewById(R.id.frg_explore_srfl);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return mView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mAdapter=new RecyclerExploreAdapter(getActivity());
        mRecyclerView.addOnScrollListener(mScrollListener);

        SlideInBottomAnimationAdapter scaleInAnimationAdapter=new SlideInBottomAnimationAdapter(mAdapter);
        scaleInAnimationAdapter.setFirstOnly(false);
//        scaleInAnimationAdapter.setDuration(800);
        mRecyclerView.setAdapter(scaleInAnimationAdapter);
        initAdapterData();
        mFabPhotoLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                openPhotoLib();
            }
        });
        mFabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                doTakePhoto();
            }
        });
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }

    /**
     * 首次载入数据
     */
    private void initAdapterData() {
        if(isInit){//载入cache
            Number number=mRealm.where(Explore.class).max("id");
            if(number==null){//本地没有缓存--->请求网络数据
                if(NetUtils.isNetConnect()){
                    loadData(true,0);
                }else {
                    Snackbar.make(mView,"无网络连接，无法从服务器上获取数据，请检查网络!",Snackbar.LENGTH_INDEFINITE).show();
                }
                
                
            }else {//本地有缓存--->加载缓存
               
                List<Explore> initData=mRealm.where(Explore.class)
                        .lessThanOrEqualTo("id",number.intValue())
                        .greaterThan("id",number.intValue()-20)
                        .findAllSorted("id",Sort.DESCENDING);
                
                if(initData==null||initData.size()<=0){//没有数据---不太可能
                    if(NetUtils.isNetConnect()){//有网络-->请求数据
                        Snackbar.make(mView,"你遇到了一个不常见的Bug，请访问：notes.wanghao.work反馈给作者吧:)",Snackbar.LENGTH_LONG).show();
                    }else {//无网络
                        Snackbar.make(mView,"你遇到了一个不常见的Bug，请访问：notes.wanghao.work反馈给作者吧:)",Snackbar.LENGTH_LONG).show();
                    }
                }else {//有数据
                    mdata.removeAll(initData);
                    mdata.addAll(initData);
                    mAdapter.updateItem(mdata);
                    endItemID=initData.get(initData.size()-1).getId();
                    firstItemID=initData.get(0).getId();
                    isInit=false;
                    //到这里缓存加载完毕接下来 看有无网络进行更新数据
                    if(NetUtils.isNetConnect()){//有网络--》更新数据
                        loadData(true,number.intValue());
                    }else {//无网络
                        Snackbar.make(mView,"无网络连接，先看看本地缓存吧!",Snackbar.LENGTH_INDEFINITE).show();
                        return;
                    }
                }
            }
            
        }
    }

    /**
     * 下拉刷新事件
     */
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        loadData(true, firstItemID);
//        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 上滑加载更多数据
     */
    private void loadMore() {
        if (isLoadMore) {
            return;
        }
        isLoadMore = true;
        Log.e("开始载入更多",String.valueOf(endItemID));
        loadData(false, endItemID);

    }

    /**
     * 载入数据函数
     *
     * @param isUpdate 是否是下拉刷新数据
     * @param startId  开始的item的id号
     */
    private void loadData(boolean isUpdate, final int startId) {


        AsyncTask<Integer, Void, Integer> loadNewDataFromRealm = new AsyncTask<Integer, Void, Integer>() {


            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(Integer integer) {
                List<Explore> realmData = null;
                if (integer == -1) {//表示网络错误，没有获取到新的数据直接返回
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(mView,"无网络连接，无法从服务器上获取数据，请检查网络!",Snackbar.LENGTH_SHORT).show();
                    return;
                } else {//有网络连接
                    if(startId==0){
                        int maxid=mRealm.where(Explore.class).max("id").intValue();
                        realmData = mRealm.where(Explore.class)
                                .lessThanOrEqualTo("id", maxid)
                                .greaterThan("id",maxid -20)
                                .findAllSorted("id", Sort.DESCENDING);
                        if(realmData.size()>20){
                            realmData=realmData.subList(0,19);
                        }
                    }else {
                        realmData = mRealm.where(Explore.class)
                                .greaterThan("id", startId)
                                .lessThanOrEqualTo("id", startId + 20)
                                .findAllSorted("id", Sort.DESCENDING);
                    }
                    
                }
               
                if (realmData==null || realmData.size() == 0) {
                    swipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(mView,"已经是最新的数据咯~",Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Log.e("loadData", "Expolre获取更新数据完成，数据量:" + String.valueOf(realmData.size()));
                mdata.removeAll(realmData);//去掉重复元素
                mdata.addAll(0, realmData);//插入到最前面
                mAdapter.updateItem(mdata);
                swipeRefreshLayout.setRefreshing(false);
                endItemID = realmData.get(realmData.size() - 1).getId();
                firstItemID = realmData.get(0).getId();
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    return RealmUtils.isNewExploreDataFromRealm(startId, getActivity());
                } else {
                    return -1;
                }
            }
        };


        AsyncTask<Integer, Void, Integer> loadOldDataFromRealm = new AsyncTask<Integer, Void, Integer>() {
            @Override
            protected void onPreExecute() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            protected void onPostExecute(Integer integer) {
                List<Explore> realmData = null;
                realmData = mRealm.where(Explore.class)
                        .lessThan("id", startId)
                        .greaterThanOrEqualTo("id", startId - 20)
                        .findAllSorted("id", Sort.DESCENDING);
               
                if (realmData==null|| realmData.size() == 0) {
                    swipeRefreshLayout.setRefreshing(false);
                    isLoadMore=false;
                    if(integer==-1){
                        Snackbar.make(mView,"没有网络，加载不出来更多数据...",Snackbar.LENGTH_SHORT).show();
                    }else {
                        Snackbar.make(mView,"已经到底咯~",Snackbar.LENGTH_SHORT).show();
                    }
                    return;
                }
                Log.e("loadData", "Explore加载更多数据完成，数据量：" + String.valueOf(realmData.size()));
                mdata.removeAll(realmData);
                mdata.addAll(realmData);
                mAdapter.updateItem(mdata);
                swipeRefreshLayout.setRefreshing(false);
                isLoadMore = false;
                endItemID = realmData.get(realmData.size() - 1).getId();
                isLoadMore=false;
            }

            @Override
            protected Integer doInBackground(Integer... params) {
                if (NetUtils.isNetConnect()) {
                    Log.e("有网络","开始载入网络数据");
                    return RealmUtils.isOldExploreDataFromRealm(startId, getActivity());
                } else {
                    Log.e("无网络","无法载入网络数据");
                    return -1;
                }
            }
        };


        if (isUpdate) {
            loadNewDataFromRealm.execute(startId);
        } else {
            loadOldDataFromRealm.execute(startId);
        }
    }

    /**
     * 监听RecycleView状态的改变
     */
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        /**
         * 滚动到底部加载更多数据
         * @param recyclerView
         * @param newState
         */
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                loadMore();//载入更多数据
            }
        }

        /**
         * 获取最后的Item好吗
         * @param recyclerView
         * @param dx
         * @param dy
         */
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
        }
    };
    
    
    
    
    
    
    
    
    public void  createUploadAlert(final File imageFile){
        View view=LayoutInflater.from(getActivity()).inflate(R.layout.alert_explore_upload_image,null);
        final EditText textContent= (EditText) view.findViewById(R.id.alert_input_text);
        GifImageView imageView= (GifImageView) view.findViewById(R.id.alert_preview_image);
        final AlertDialog.Builder contentAlert=new AlertDialog.Builder(getActivity());
        contentAlert.setView(view);
        contentAlert.setCancelable(false);
        Glide.with(this).load(imageFile).asBitmap().into(imageView);
        Token token= SQLiteUtils.getCurrentLoginUserToken(getActivity());
        String strToken=null;
        if (token == null) {
            Snackbar.make(mView, "将以游客的身份上传图片", Snackbar.LENGTH_SHORT).show();
        }else {
            strToken=token.getToken();
        }
        
        final String finStrToken=strToken;
        contentAlert.setPositiveButton("上传", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String commentContent = textContent.getText().toString().trim();
                if (commentContent.length() <= 0) {
                    Snackbar.make(mView, "起码你也得说两句呗~", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                AsyncTask<String, Void, Boolean> postComment = new AsyncTask<String, Void, Boolean>() {
                    @Override
                    protected void onPostExecute(Boolean aBoolean) {
                        if (aBoolean) {
                            Snackbar.make(mView, "上传成功", Snackbar.LENGTH_SHORT).show();
                            onRefresh();

                        } else {
                            Snackbar.make(mView, "因为一些错误导致上传失败", Snackbar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    protected Boolean doInBackground(String... params) {
                        if (NetUtils.isNetConnect()) {
                            return HttpUtils.uploadImageToServer(params[0], params[1], imageFile);
                        } else {
                            return false;
                        }
                    }
                };
                postComment.execute(finStrToken, commentContent);
            }
        });
        contentAlert.setNegativeButton("取消", null);
        contentAlert.show();
    }


    /**
     * 拍照函数
     */
    public void doTakePhoto(){
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String path=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"XiaMo";
        File file=new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        Uri imageUri=Uri.fromFile(new File(path+File.separator+GlobalConfig.TEMP_CAMERA_IMG));
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intent, GlobalConfig.PAHTO_WITH_CAMERA);
        
    }
    
    public void openPhotoLib(){
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,GlobalConfig.CHOOSE_PHOTO);
    }

    
    public String getRealPathFromUri(Uri contentUri){
        String result=null;
        try {
            
        
        Cursor cursor=getActivity().getContentResolver().query(contentUri,null,null,null,null);
        if(cursor==null){
            result =contentUri.getPath();
        }else {
            cursor.moveToFirst();
            int idx=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result=cursor.getString(idx);
            cursor.close();
        }
        }catch (Exception e){
            Toast.makeText(getActivity(),"请使用系统相册选择照片",Toast.LENGTH_LONG).show();
        }
        return result;
    }
    

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==GlobalConfig.RESULT_OK){
            switch (requestCode){
                case GlobalConfig.PAHTO_WITH_CAMERA:
                    String cemareImageFilePath=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator
                            +GlobalConfig.DIR_NAME+File.separator+GlobalConfig.TEMP_CAMERA_IMG;
                    File file=new File(cemareImageFilePath);
                    if(file.exists()){
                        createUploadAlert(file);
                        Log.e("执行了s", "执s行了");
                    }else {
                        return;
                    }
                    break;
                case GlobalConfig.CHOOSE_PHOTO:
                    Uri photoUri=data.getData();
                    String imageFilePath=getRealPathFromUri(photoUri);
                    if(imageFilePath==null){
                        Log.e("文件路径为空","文件路径为空");
                       return;
                    }else {
                        Log.e("执行了", "执行了");
                        File imageFile=new File(imageFilePath);
                        
                        if(!imageFile.exists()){
                            Log.e("文件不存在","文件不存在");
                            return;
                        }else {
                            createUploadAlert(imageFile);
                        }
                    }
                  
//                    Log.e("frg图片的路径为",);
                    break;
            }
        }
    }
}
