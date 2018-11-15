package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMemberFavoriteDao;
import www.ucforward.com.entity.HsMemberFavorite;

import java.util.Map;

/**
 * Created by Administrator on 2018/6/8.
 */
@Repository("memberFavoriteDao")
public class HsMemberFavoriteDaoImpl extends BaseDao implements HsMemberFavoriteDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMemberFavoriteDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsMemberFavorite record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMemberFavorite record) {
        return this.getSqlSession().insert("HsMemberFavoriteDao.insertSelective",record);
    }

    @Override
    public HsMemberFavorite selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMemberFavoriteDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMemberFavorite record) {
        return this.getSqlSession().update("HsMemberFavoriteDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsMemberFavorite record) {
        return this.getSqlSession().update("HsMemberFavoriteDao.updateByPrimaryKey",record);
    }

    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        String sql = "HsMemberFavoriteDao.selectHsMemberFavoriteListMapByCondition";
        if(returnType != 0){
            sql = "HsMemberFavoriteDao.selectHsMemberFavoriteListByCondition";
        }
        return executeSql(condition,sql);
    }
}
