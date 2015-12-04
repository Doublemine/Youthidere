package work.wanghao.youthidere.db;

import android.os.AsyncTask;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.Sort;
import work.wanghao.youthidere.model.PostItem;
import work.wanghao.youthidere.utils.HttpUtils;

/**
 * Created by wangh on 2015-12-1-0001.
 */
public class RealmDB {


    private Realm mRealm;

    private final static String BROWSER_UA = "Mozilla/5.0 (Linux U 10.0; Android 5.0;zh-CN; Doublemine Build/2015) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.86 Safari/537.36";

    private static final int DEFAULT_DATA_LENGTH = 20;
    private RealmConfiguration realmConfiguration;


    public RealmDB(RealmConfiguration realmConfiguration) {
        this.realmConfiguration = realmConfiguration;
        this.mRealm = Realm.getInstance(realmConfiguration);
    }

    
    public void Destory(){
        this.mRealm.close();
    }
    
    /**
     * @param startId    起始ID
     * @param isLoadMore 是否是载入更多
     * @param dataLength 数据长度
     * @return
     */
    public List<PostItem> getPostItemResult(final int startId, boolean isLoadMore, int dataLength) {

        final boolean[] isNull = new boolean[1];
        List<PostItem> _data = null;
        if (isLoadMore) {//上滑加载更多数据
            int minId = startId - dataLength;
            if (minId <= 0) {
                minId = 0;
            }
            _data = mRealm.where(PostItem.class)
                    .lessThan("id", startId)
                    .greaterThanOrEqualTo("id", minId)
                    .findAllSorted("id", Sort.DESCENDING);

            /**
             * 1.获取网络数据-->保存数据库--->查询--->返回
             * 2.获取网络数据-->返回===>保存数据库
             */

            if (_data == null || _data.size() < dataLength) {//获取网络数据
                new AsyncTask<Integer, Void, List<PostItem>>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(List<PostItem> items) {
                        if (items == null) {//服务器数据和本地数据一致
                            isNull[0] = true;
                            return;
                        } else {
                            isNull[0] = false;
                            mRealm.beginTransaction();
                            mRealm.copyToRealmOrUpdate(items);
                            mRealm.commitTransaction();
                            //写入完成
                        }
                    }

                    @Override
                    protected List<PostItem> doInBackground(Integer... params) {
                        return HttpUtils.getOldImgDataFromServer(params[0]);
                    }
                }.execute(startId);
            }
            if (isNull[0]) {
                return _data;
            } else {
                _data = mRealm.where(PostItem.class)
                        .lessThan("id", startId)
                        .greaterThanOrEqualTo("id", minId)
                        .findAllSorted("id", Sort.DESCENDING);
            }
        } else {//下拉刷新加载数据
            /**
             * 1. 删除所有缓存数据
             * 2. 从网络获取数据
             * 3. 写入数据库
             * 4. 查询数据库
             * 5. 返回结果
             */
            new AsyncTask<Integer, Void, List<PostItem>>() {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected void onPostExecute(List<PostItem> items) {
                    if (items == null) {//服务器没有新数据
                        isNull[0] = true;
                        return;
                    } else {
                        isNull[0] = false;
                        mRealm.beginTransaction();
                        mRealm.copyToRealm(items);
                        mRealm.commitTransaction();
                    }
                }

                @Override
                protected List<PostItem> doInBackground(Integer... params) {
                    return HttpUtils.getOldImgDataFromServer(params[0]);
                }
            }.execute(startId);

            if (isNull[0]) {
                return null;
            } else {
                _data = mRealm.where(PostItem.class)
                        .greaterThan("id", startId)
                        .lessThanOrEqualTo("id", startId + DEFAULT_DATA_LENGTH)
                        .findAllSorted("id", Sort.DESCENDING);
            }

        }


        return _data;

    }

    /**
     * 获取当前数据库中最新的数据，并返回
     *
     * @return
     */
    public List<PostItem> getCacheWhenCreate() {
        int maxFlag = (int) mRealm.where(PostItem.class).max("id");
        int minFlag = maxFlag - DEFAULT_DATA_LENGTH;
        if (maxFlag - DEFAULT_DATA_LENGTH <= 0) {
            minFlag = 0;
        }
        List<PostItem> items = mRealm.where(PostItem.class)
                .lessThanOrEqualTo("id", maxFlag)
                .greaterThan("id", minFlag)
                .findAllSorted("id", Sort.DESCENDING);

        return items;
    }
}
