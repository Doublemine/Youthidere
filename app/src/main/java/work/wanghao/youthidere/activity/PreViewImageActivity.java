package work.wanghao.youthidere.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.File;

import pl.droidsonroids.gif.GifImageView;
import uk.co.senab.photoview.PhotoViewAttacher;
import work.wanghao.youthidere.R;
import work.wanghao.youthidere.config.GlobalConfig;
import work.wanghao.youthidere.utils.ShareUtils;

/**
 * Created by wangh on 2015-12-10-0010.
 */

public class PreViewImageActivity extends AppCompatActivity {

    private GifImageView mImage;
    private FloatingActionMenu mFabMenu;
    private FloatingActionButton mFabSave;
    private String src;
    private static final String IMG_DIR = "XiaMo";
    private PhotoViewAttacher mAttacher;
    private boolean isSaving;
    private boolean isSaved;
    private FloatingActionButton mFabShare;
    private String mFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        isSaving = false;
        isSaved = false;

        mFabSave = (FloatingActionButton) findViewById(R.id.fab_menu_save);
        mImage = (GifImageView) findViewById(R.id.activity_preview_image);
        mFabShare = (FloatingActionButton) findViewById(R.id.fab_menu_share);
        mFabMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        Intent intent = getIntent();
        src = intent.getStringExtra("img_url");
        mFileName = src.substring(src.lastIndexOf('/') + 1);
        Log.e("saveImageToSDCard", src);
        Glide.with(this).load(src).placeholder(R.raw.loading).diskCacheStrategy(DiskCacheStrategy.SOURCE).priority(Priority.HIGH).into(mImage);
        mFabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFabMenu.close(true);
                saveImageToSDCard();

            }
        });
        mFabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSaved) {
                    Toast.makeText(PreViewImageActivity.this, "请先保存再分享~", Toast.LENGTH_SHORT).show();
                    return;
                }
                mFabMenu.close(true);
                ShareUtils.shareImage(PreViewImageActivity.this, Uri.fromFile(new File(GlobalConfig.APP_IMGAGE_STORE_DIR_PATH + mFileName)), "分享到...");
            }
        });
        mAttacher = new PhotoViewAttacher(mImage);
        mAttacher.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAttacher.cleanup();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void saveImageToSDCard() {
        if (isSaving) {
            Toast.makeText(PreViewImageActivity.this, "客官不要急，正在保存呢~", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isSaved) {
            Toast.makeText(PreViewImageActivity.this, "文件已经保存过啦~", Toast.LENGTH_SHORT).show();
            return;
        }

        final String imgDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + IMG_DIR;
        File file = new File(imgDir);
        if (!file.exists()) {
            file.mkdir();
        }


        if (src.endsWith(".gif")) {
            isSaving = true;
            Glide.with(this).load(src).asGif().into(new SimpleTarget<GifDrawable>() {
                @Override
                public void onResourceReady(GifDrawable resource, GlideAnimation<? super GifDrawable> glideAnimation) {

                    AsyncTask<GifDrawable, Void, Boolean> storeTask = new AsyncTask<GifDrawable, Void, Boolean>() {
                        @Override
                        protected void onPreExecute() {
                            isSaving = true;
                            isSaved = false;
                        }

                        @Override
                        protected void onPostExecute(Boolean aVoid) {
                            if (aVoid) {
                                isSaving = false;
                                isSaved = true;
                                Toast.makeText(PreViewImageActivity.this, "文件成功保存在SDCard的XiaMo目录下", Toast.LENGTH_LONG).show();
                            } else {
                                isSaving = false;
                                isSaved = false;
                                Toast.makeText(PreViewImageActivity.this, "文件保存失败", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        protected Boolean doInBackground(GifDrawable... params) {
                            return ShareUtils.saveGifFile(GlobalConfig.APP_IMGAGE_STORE_DIR_PATH + mFileName, params[0].getData());
                        }

                    };

                    storeTask.execute(resource);

                }
            });


        } else {
            isSaving = true;
//            final String filename=src.substring(src.lastIndexOf('/')+1);
//            Log.e("文件名为:",filename);
//            Log.e("当前路径",src);
            Glide.with(this).load(src).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    AsyncTask<Bitmap, Void, Boolean> storeTask = new AsyncTask<Bitmap, Void, Boolean>() {
                        @Override
                        protected void onPreExecute() {
                            isSaving = true;
                            isSaved = false;
                        }

                        @Override
                        protected void onPostExecute(Boolean aVoid) {
                            if (aVoid) {
                                isSaving = false;
                                isSaved = true;
                                Toast.makeText(PreViewImageActivity.this, "文件成功保存在SDCard的XiaMo目录下", Toast.LENGTH_LONG).show();
                            } else {
                                isSaving = false;
                                isSaved = false;
                                Toast.makeText(PreViewImageActivity.this, "文件保存失败", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        protected Boolean doInBackground(Bitmap... params) {
                            return ShareUtils.saveBitmapFile(GlobalConfig.APP_IMGAGE_STORE_DIR_PATH + mFileName, params[0]);
                        }

                    };
                    storeTask.execute(resource);
                }
            });
            

        }


    }

}
