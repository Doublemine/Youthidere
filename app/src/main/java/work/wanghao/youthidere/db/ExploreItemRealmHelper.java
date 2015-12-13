package work.wanghao.youthidere.db;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by wangh on 2015-12-3-0003.
 */
public class ExploreItemRealmHelper {
    public static Realm getRealm(Context context){
        RealmConfiguration configuration = new RealmConfiguration.Builder(context)
                .name("explore.realm")
                .schemaVersion(0)
                .build();
        Realm realm = Realm.getInstance(configuration);
        return realm;
    }
}
