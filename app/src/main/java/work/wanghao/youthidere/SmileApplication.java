package work.wanghao.youthidere;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.integration.okhttp.OkHttpUrlLoader;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper;
import com.bumptech.glide.load.model.GlideUrl;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.InputStream;

/**
 * Created by wangh on 2015-11-27-0027.
 */
public class SmileApplication extends Application {
    


    
    @Override
    public void onCreate() {
        super.onCreate();
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(new OkHttpClient()));
        new GlideBuilder(this)
                .setDiskCache(new DiskCache.Factory() {
                    @Override
                    public DiskCache build() {
                        // Careful: the external cache directory doesn't enforce permissions
                        File cacheLocation = new File(getExternalCacheDir(), "cache_dir_name");
                        cacheLocation.mkdirs();
                        return DiskLruCacheWrapper.get(cacheLocation, Integer.parseInt(getSharedPreferences("work.wanghao.youthidere_preferences", MODE_PRIVATE).getString("cache_size", "50")) * 1024 * 1024);
                    }
                });
        new GlideBuilder(this)
                .setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
