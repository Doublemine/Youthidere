package work.wanghao.youthidere.utils;

import android.content.Context;
import android.util.Log;

import java.util.List;

import work.wanghao.youthidere.dao.TokenDaoImpl;
import work.wanghao.youthidere.model.Token;

/**
 * Created by wangh on 2015-11-29-0029.
 * 
 * 建议查询在UI线程上
 * 写入在后台线程上
 */
public class DbUtils {


    
    public static Token getCurrentLoginUserToken(Context context){
        List<Token> list=TokenDaoImpl.getInstance(context).selectAllToken();
        if(list==null){
            return null;
        }
        int num=list.size();
        Log.e("--------->",String.valueOf(num));
        if(num<=0){
            return null;
        }else{
           return list.get(list.size()-1);
        }
    }

    public static int getTokenNum(Context context) {
        
        List<Token> list=TokenDaoImpl.getInstance(context).selectAllToken();
        if(list==null){
            return 0;
        }
        return list.size();
    }
}
