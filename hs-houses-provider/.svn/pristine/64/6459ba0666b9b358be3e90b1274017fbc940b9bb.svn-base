package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHouseComplainDao;
import www.ucforward.com.entity.HsHouseComplain;

import java.util.Map;

/**
 * Created by Administrator on 2018/6/15.
 */
@Repository("houseComplainDao")
public class HsHouseComplainDaoImpl extends BaseDao implements HsHouseComplainDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHouseComplainDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHouseComplain record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHouseComplain record) {
        return this.getSqlSession().insert("HsHouseComplainDao.insertSelective",record);
    }

    @Override
    public HsHouseComplain selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHouseComplainDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHouseComplain record) {
        return this.getSqlSession().update("HsHouseComplainDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHouseComplain record) {
        return this.getSqlSession().update("HsHouseComplainDao.updateByPrimaryKey",record);
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
        String sql  = "HsHouseComplainDao.selectListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsHouseComplainDao.selectListByCondition";
        }
        return executeSql(condition, sql);
    }

}
