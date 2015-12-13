package work.wanghao.youthidere.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by wangh on 2015-12-5-0005.
 */
public class TabCategoriesHelper {

    public static Realm getRealm(Context context){
        RealmConfiguration configuration = new RealmConfiguration.Builder(context)
                .name("categories.realm")
                .schemaVersion(0)
                .build();
        Realm realm = Realm.getInstance(configuration);
        return realm;
    }
}
