package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSystemOrderPoolDao;
import www.ucforward.com.entity.HsSystemOrderPool;

import java.util.List;
import java.util.Map;

@Repository("hsSystemOrderPoolDao")
public class HsSystemOrderPoolDaoImpl extends BaseDao implements HsSystemOrderPoolDao {


    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsSystemOrderPoolDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsSystemOrderPool record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsSystemOrderPool record) {
        return this.getSqlSession().insert("HsSystemOrderPoolDao.insertSelective",record);
    }

    @Override
    public HsSystemOrderPool selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsSystemOrderPoolDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSystemOrderPool record) {
        return this.getSqlSession().update("HsSystemOrderPoolDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsSystemOrderPool record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 查询列表数据
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     * @throws Exception
     */
    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        //默认查询ListMap
        String sql  = "HsSystemOrderPoolDao.selectSystemOrderPoolListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsSystemOrderPoolDao.selectSystemOrderPoolListByCondition";
        }
        return executeSql(condition,sql);
    }

    /**
     * 批量修改数据
     * @param data
     * @return
     */
    @Override
    public int batchUpdate(List<HsSystemOrderPool> data) {
        return this.getSqlSession().update("HsSystemOrderPoolDao.batchUpdate",data);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsSystemOrderPoolDao.selectCustomColumnNamesList");
    }
}