package work.wanghao.youthidere.utils;

import java.text.SimpleDateFormat;

/**
 * Created by wangh on 2015-11-30-0030.
 */
public class DateUtils {
    private static SimpleDateFormat dateFormat;
    
    public static String getDateWithDay(String date){
        if(dateFormat==null){
            dateFormat=new SimpleDateFormat("YYYY年MM月dd日");
        }
        return dateFormat.format(date);
    }
}
