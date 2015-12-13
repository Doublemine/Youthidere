package work.wanghao.youthidere.app;

import android.app.Application;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.squareup.okhttp.OkHttpClient;

import java.io.InputStream;

/**
 * Created by wangh on 2015-11-27-0027.
 */
public class SmileApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

//        Log.e("打印信息", Glide.getPhotoCacheDir(this).toString());
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient()));
        Log.e("打印信息", Glide.getPhotoCacheDir(this).toString());
//        SharedPreferences pre = PreferenceManager.getDefaultSharedPreferences(this);
//        Log.e("pre",String.valueOf(pre.getBoolean("log_history",false)));
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
