package work.wanghao.youthidere.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by wangh on 2015-11-28-0028.
 */
public class DataBaseOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "tuzhai.db";
    public static final int DB_VERSION = 1;
    public static final String TAB_TOKEN = "tab_token";
    public static final String TAB_POST_ITEM = "tab_post_item";


    public DataBaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String dropTabTokenSql = "DROP TABLE IF EXISTS " + TAB_TOKEN;
        String dropTabPostItemSql = "DROP TABLE IF EXISTS " + TAB_POST_ITEM;
        db.execSQL(dropTabPostItemSql);
        db.execSQL(dropTabTokenSql);
        String createTabTokenSql = "create table " + TAB_TOKEN
                + " ("
                + "id INTEGER NOT NULL PRIMARY KEY ,"
                + "uuid TEXT ,"
                + "name  TEXT NOT NULL,"
                + "email  TEXT NOT NULL,"
                + "sex  INTEGER NOT NULL,"
                + "score INTEGER NOT NULL,"
                + "avatar_url  TEXT NOT NULL,"
                + "created_at  TEXT NOT NULL,"
                + "post_read_count  INTEGER NOT NULL,"
                + "comments_count  INTEGER NOT NULL,"
                + "favo_count  INTEGER NOT NULL,"
                + "weixin  INTEGER NOT NULL,"
                + "qq  INTEGER NOT NULL,"
                + "token  TEXT NOT NULL "
                + ")";

        String createTabPostItemSql = "create table " + TAB_POST_ITEM
                + " ("
                + "id  INTEGER NOT NULL PRIMARY KEY,"
                + "user_id  INTEGER NOT NULL,"
                + "category_slug  TEXT NOT NULL,"
                + "main_img  TEXT NOT NULL,"
                + "title  TEXT NOT NULL,"
                + "views_count  TEXT NOT NULL,"
                + "comments_count  INTEGER NOT NULL,"
                + "created_at  TEXT NOT NULL,"
                + "user_name  TEXT NOT NULL,"
                + "user_email  TEXT,"
                + "user_avatar_url  TEXT NOT NULL,"
                + "user_created_at  TEXT NOT NULL,"
                + "category_icon  TEXT NOT NULL,"
                + "category_name  TEXT NOT NULL,"
                + "post_counts  INTEGER NOT NULL"
                + " )";
        db.execSQL(createTabPostItemSql);
        db.execSQL(createTabTokenSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
