package work.wanghao.youthidere.model;

import java.util.List;

import work.wanghao.youthidere.model.AccountFavorite;

/**
 * Created by wangh on 2015-12-12-0012.
 */
public class ReceAccountFavoriteJsonData {
    private List<AccountFavorite> favorites;

    public List<AccountFavorite> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<AccountFavorite> favorites) {
        this.favorites = favorites;
    }
}
