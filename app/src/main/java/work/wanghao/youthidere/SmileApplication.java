package work.wanghao.youthidere;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;

/**
 * Created by wangh on 2015-11-27-0027.
 */
public class SmileApplication extends Application {
    

//    public static Realm postItemRealm;
    
    @Override
    public void onCreate() {
        super.onCreate();
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient()));
//        RealmConfiguration postItemConfiguration=new RealmConfiguration.Builder(this).name("postItem.realm").build();
//        postItemRealm=Realm.getGsonInstance(postItemConfiguration);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
//        postItemRealm.close();
    }
}
