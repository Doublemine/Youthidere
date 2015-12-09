package work.wanghao.youthidere.utils;

import android.text.TextUtils;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangh on 2015-11-30-0030.
 */
public class DateUtils {
   
    private static SimpleDateFormat sdfres;
    private static SimpleDateFormat sdfdes;
    
    private static SimpleDateFormat getSdfres(){
        if(sdfres==null){
            sdfres=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        }
        return sdfres;
    }
    private static SimpleDateFormat getSdfdesWithoutHours(){
        if(sdfdes==null){
            sdfdes=new SimpleDateFormat("yyyy年MM月dd日");
        }
        return sdfdes;
    }

    private static SimpleDateFormat getSdfdesWithHours(){
        if(sdfdes==null){
            sdfdes=new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        }
        return sdfdes;
    }
    
    
    public static String formatDateFromStrWithoutHour(String dateStr){
       String strDate=null;
        if(!TextUtils.isEmpty(dateStr)){
            SimpleDateFormat sdfres=getSdfres();
            SimpleDateFormat sdfdes= getSdfdesWithoutHours();
            try {
                Date date=sdfres.parse(dateStr);
                strDate=sdfdes.format(date);
            }catch (Exception e){
                Log.e("DateUtils","时间转换错误:"+e.toString());
            }
        }
        return strDate;
    }

    public static String formatDateFromStrWithHour(String dateStr){
        String strDate=null;
        if(!TextUtils.isEmpty(dateStr)){
            SimpleDateFormat sdfres=getSdfres();
            SimpleDateFormat sdfdes= getSdfdesWithHours();
            try {
                Date date=sdfres.parse(dateStr);
                strDate=sdfdes.format(date);
            }catch (Exception e){
                Log.e("DateUtils","时间转换错误:"+e.toString());
            }
        }
        return strDate;
    }
    
   
}
