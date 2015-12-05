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
    private static SimpleDateFormat getSdfdes(){
        if(sdfdes==null){
            sdfdes=new SimpleDateFormat("yyyy年MM月dd日");
        }
        return sdfdes;
    }
    
    
    public static String formatDateFromStr(String dateStr){
       String strDate=null;
        if(!TextUtils.isEmpty(dateStr)){
            SimpleDateFormat sdfres=getSdfres();
            SimpleDateFormat sdfdes=getSdfdes();
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
