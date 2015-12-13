package work.wanghao.youthidere.config;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by wangh on 2015-12-11-0011.
 */
public class GlideConfig implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
      final Context context1=context;
        if( PreferenceManager.getDefaultSharedPreferences(context1).getBoolean("image_quality",false)){
            builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
            Log.e("设置为了","PREFER_ARGB_8888");
        }
        
       final String CustomDiskSize =PreferenceManager.getDefaultSharedPreferences(context1).getString("cache_size", "50");
        if(PreferenceManager.getDefaultSharedPreferences(context1).getBoolean("internal_or_external_cache",true))
        {
            builder.setDiskCache(
                    new InternalCacheDiskCacheFactory(context, "xiamo_image_cache", Integer.parseInt(CustomDiskSize)*1024*1024));
        }else {
            builder.setDiskCache(
                    new ExternalCacheDiskCacheFactory(context, "xiamo_image_cache", Integer.parseInt(CustomDiskSize)*1024*1024));
        }
        
        Log.e("设置为了",String.valueOf(CustomDiskSize));
//        builder.setDiskCache(new DiskCache.Factory() {
//            @Override
//            public DiskCache build() {
//                File cacheLocation = new File(context1.getExternalCacheDir(), "cache_dir_name");
//                cacheLocation.mkdirs();
//                return DiskLruCacheWrapper.get(cacheLocation, Integer.parseInt(context1.getSharedPreferences("work.wanghao.youthidere_preferences", context1.MODE_PRIVATE).getString("cache_size", "50")) * 1024 * 1024);
//            }
//        });
       
       
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
          
    }
}


//
//builder.setDiskCache(new DiskCache.Factory() {
//@Override
//public DiskCache build() {
//        // Careful: the external cache directory doesn't enforce permissions
//        File cacheLocation = new File(getExternalCacheDir(), "cache_dir_name");
//        cacheLocation.mkdirs();
//        return DiskLruCacheWrapper.get(cacheLocation, Integer.parseInt(getSharedPreferences("work.wanghao.youthidere_preferences", MODE_PRIVATE).getString("cache_size", "50")) * 1024 * 1024);
//        }
//        });
//        builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
//        builder.setMemoryCache(new LruResourceCache(0));
//        if(!Glide.isSetup()){
//        Glide.setup(builder);
//        }
//        Log.e("打印信息", Glide.getPhotoCacheDir(this).toString());
//        
