package work.wanghao.youthidere.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import work.wanghao.youthidere.model.Token;
import work.wanghao.youthidere.model.User;

/**
 * Created by wangh on 2015-11-28-0028.
 */
public class TokenDaoImpl implements TokenDao {

    private DataBaseOpenHelper mSqlHelper;
    private static TokenDaoImpl tokenDao;

    public static TokenDaoImpl getInstance(Context context) {
        if (tokenDao == null) {
            tokenDao = new TokenDaoImpl(context);
        }
        return tokenDao;
    }

    private TokenDaoImpl(Context context) {
        mSqlHelper = new DataBaseOpenHelper(context);
    }

    @Override
    public void insertToken(Token token) throws SQLException {
        synchronized (this){
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id", token.getUser().getId());
        values.put("uuid", token.getUser().getUuid());
        values.put("name", token.getUser().getName());
        values.put("email", token.getUser().getEmail());
        values.put("sex", token.getUser().getSex());
        values.put("score", token.getUser().getScore());
        values.put("avatar_url", token.getUser().getAvatar_url());
        values.put("post_read_count", token.getUser().getPost_read_count());
        values.put("comments_count", token.getUser().getComments_count());
        values.put("created_at", token.getUser().getCreated_at());
        values.put("favo_count", token.getUser().getFavo_count());
        values.put("weixin", token.getUser().isWeixin());
        values.put("qq", token.getUser().isQq());
        values.put("token", token.getToken());
        try {
            db.insert(mSqlHelper.TAB_TOKEN, null, values);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                Log.e("插入完成", "插入数据库完成");
            }
        }
        }
    }

    @Override
    public void updateToken(Token token) throws SQLException {
        synchronized (this) {
            SQLiteDatabase db = mSqlHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", token.getUser().getId());
            values.put("uuid", token.getUser().getUuid());
            values.put("name", token.getUser().getName());
            values.put("email", token.getUser().getEmail());
            values.put("sex", token.getUser().getSex());
            values.put("score", token.getUser().getScore());
            values.put("avatar_url", token.getUser().getAvatar_url());
            values.put("post_read_count", token.getUser().getPost_read_count());
            values.put("comments_count", token.getUser().getComments_count());
            values.put("favo_count", token.getUser().getFavo_count());
            values.put("weixin", token.getUser().isWeixin());
            values.put("created_at", token.getUser().getCreated_at());
            values.put("qq", token.getUser().isQq());
            values.put("token", token.getToken());
            String whereCause = " id = ?";
            String[] whereArgs = new String[]{String.valueOf(token.getUser().getId())};
            try {
                db.update(DataBaseOpenHelper.TAB_TOKEN, values, whereCause, whereArgs);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                    Log.e("更新完成", "更新数据库完成");
                }
            }
        }

    }

    @Override
    public void deleteToken(Token token) throws SQLException {
        synchronized (this) {
            SQLiteDatabase db = mSqlHelper.getWritableDatabase();
            try {
                String whereClause = " id = ? ";
                String[] whereArgs = new String[]{String.valueOf(token.getUser().getId())};
                db.delete(DataBaseOpenHelper.TAB_TOKEN, whereClause, whereArgs);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        }
    }

    @Override
    public Token selectToken(Token token) throws SQLException {
        Token result = null;
        User user = null;
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        String sql = "SELECT * FROM " + DataBaseOpenHelper.TAB_TOKEN + " WHERE id = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, new String[]{String.valueOf(token.getUser().getId())});
            while (cursor.moveToNext()) {
                result = new Token();
                user = new User();
                result.setToken(cursor.getString(cursor.getColumnIndex("token")));
                user.setId(cursor.getInt(cursor.getColumnIndex("id")));
                user.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
                user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                user.setSex(cursor.getInt(cursor.getColumnIndex("sex")));
                user.setScore(cursor.getInt(cursor.getColumnIndex("score")));
                user.setAvatar_url(cursor.getString(cursor.getColumnIndex("avatar_url")));
                user.setCreated_at(cursor.getString(cursor.getColumnIndex("created_at")));
                user.setPost_read_count(cursor.getInt(cursor.getColumnIndex("post_read_count")));
                user.setComments_count(cursor.getInt(cursor.getColumnIndex("comments_count")));
                user.setFavo_count(cursor.getInt(cursor.getColumnIndex("favo_count")));
                if (cursor.getInt(cursor.getColumnIndex("weixin")) == 0) {
                    user.setWeixin(false);
                } else {
                    user.setWeixin(true);
                }

                if (cursor.getInt(cursor.getColumnIndex("qq")) == 0) {
                    user.setQq(false);
                } else {
                    user.setQq(true);
                }
                result.setUser(user);
            }
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return result;
    }

    @Override
    public List<Token> selectAllToken() throws SQLException {
        SQLiteDatabase db = mSqlHelper.getWritableDatabase();
        List<Token> list = new ArrayList<Token>();
        String sql = "select * from " + DataBaseOpenHelper.TAB_TOKEN;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                Token token = new Token();
                User user = new User();

                token.setToken(cursor.getString(cursor.getColumnIndex("token")));
                user.setId(cursor.getInt(cursor.getColumnIndex("id")));
                user.setUuid(cursor.getString(cursor.getColumnIndex("uuid")));
                user.setName(cursor.getString(cursor.getColumnIndex("name")));
                user.setEmail(cursor.getString(cursor.getColumnIndex("email")));
                user.setSex(cursor.getInt(cursor.getColumnIndex("sex")));
                user.setScore(cursor.getInt(cursor.getColumnIndex("score")));
                user.setAvatar_url(cursor.getString(cursor.getColumnIndex("avatar_url")));
                user.setCreated_at(cursor.getString(cursor.getColumnIndex("created_at")));
                user.setPost_read_count(cursor.getInt(cursor.getColumnIndex("post_read_count")));
                user.setComments_count(cursor.getInt(cursor.getColumnIndex("comments_count")));
                user.setFavo_count(cursor.getInt(cursor.getColumnIndex("favo_count")));
                if (cursor.getInt(cursor.getColumnIndex("weixin")) == 0) {
                    user.setWeixin(false);
                } else {
                    user.setWeixin(true);
                }
                if (cursor.getInt(cursor.getColumnIndex("qq")) == 0) {
                    user.setQq(false);
                } else {
                    user.setQq(true);
                }
                token.setUser(user);
                list.add(token);

            }
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return list;
    }

    @Override
    public void deleteAllToken() throws SQLException {
        synchronized (this) {
            SQLiteDatabase db = mSqlHelper.getWritableDatabase();
            String sql = " DELETE FROM " + DataBaseOpenHelper.TAB_TOKEN;
            try {
                db.execSQL(sql);
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();
                }
            }
        }
    }
}
