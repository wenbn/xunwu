package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHousesBrowseHistoryDao;
import www.ucforward.com.entity.HsHousesBrowseHistory;

import java.util.Map;

/**
 * Created by Administrator on 2018/6/7.
 */

@Repository("browseHistoryDao")
public class HsHousesBrowseHistoryDaoImpl extends BaseDao implements HsHousesBrowseHistoryDao{
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHousesBrowseHistoryDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHousesBrowseHistory record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHousesBrowseHistory record) {
        return this.getSqlSession().insert("HsHousesBrowseHistoryDao.insertSelective",record);
    }

    @Override
    public HsHousesBrowseHistory selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHousesBrowseHistoryDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHousesBrowseHistory record) {
        return this.getSqlSession().update("HsHousesBrowseHistoryDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHousesBrowseHistory record) {
        return this.getSqlSession().update("HsHousesBrowseHistoryDao.updateByPrimaryKey",record);
    }

    /**
     * 查询列表数据
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return Map<Object,Object> result  key data,pageInfo
     * @throws Exception
     */
    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        //默认查询ListMap
        String sql  = "HsHousesBrowseHistoryDao.selectHsHousesBrowseHistoryListMapByCondition";
        if(returnType != 0){//查询实体
            sql  = "HsHousesBrowseHistoryDao.selectHsHousesBrowseHistoryListByCondition";
        }
        return executeSql(condition, sql);
    }

}
