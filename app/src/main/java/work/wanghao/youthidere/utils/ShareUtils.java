package work.wanghao.youthidere.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by wangh on 2015-12-13-0013.
 */
public class ShareUtils {
    
    public static void share(String imgPath,String content,Context context){
        File file=new File(imgPath);
        Uri uri=Uri.fromFile(file);
        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        if(uri!=null&&imgPath!=null&&imgPath!=""){
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.setType("image/*");
            // 当用户选择短信时使用sms_body取得文字  
            shareIntent.putExtra("sms_body", "我正在使用XiaMo开发的APP给你分享图片哦，你也来试试吧~");
        }else {
            shareIntent.setType("text/plain");
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(shareIntent, "选一个APP来分享吧~"));
//        context.startActivity(shareIntent);
    }

    public static void shareImage(Context context, Uri uri, String title) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, title));
    }
    
    

    /**
     * 保存gif函数
     * @param filePathname 文件名（包含绝对路径） /sdcard/xiamo/xxxxx.gif
     * @param dataSource 源字节流
     * @return
     */
    public static boolean saveGifFile(String filePathname,byte[] dataSource){
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataSource);
            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePathname));
            int n=0;
            byte[] bb = new byte[1024];
            while ((n=byteArrayInputStream.read(bb))!=-1){
                fileOutputStream.write(bb,0,n);
            }
            fileOutputStream.close();
            byteArrayInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
           return false;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 保存bitmap，参数同上
     * @param filePathName
     * @param resource
     * @return
     */
    public static boolean saveBitmapFile(String filePathName,Bitmap resource){
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePathName));
            resource.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    
    
    
}
