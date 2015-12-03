package work.wanghao.youthidere.dao;

import android.database.SQLException;

import java.util.List;

import work.wanghao.youthidere.model.Token;

/**
 * Created by wangh on 2015-11-28-0028.
 */
public interface TokenDao {

    void insertToken(Token token) throws SQLException;

    void updateToken(Token token) throws SQLException;

    void deleteToken(Token token) throws SQLException;

    Token selectToken(Token token) throws SQLException;

    List<Token> selectAllToken() throws SQLException;
    
    void deleteAllToken() throws SQLException;
    
    


}
