package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsFeedbackDao;
import www.ucforward.com.entity.HsFeedback;

import java.util.Map;

/**
 * Created by Administrator on 2018/6/11.
 */
@Repository("hsFeedbackDao")
public class HsFeedbackDaoImpl extends BaseDao implements HsFeedbackDao{
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsFeedbackDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsFeedback record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsFeedback record) {
        return this.getSqlSession().insert("HsFeedbackDao.insertSelective",record);
    }

    @Override
    public HsFeedback selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsFeedbackDao.selectByPrimaryKey",id);
    }


    @Override
    public int updateByPrimaryKeySelective(HsFeedback record) {
        return this.getSqlSession().update("HsFeedbackDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsFeedback record) {
        return this.getSqlSession().update("HsFeedbackDao.updateByPrimaryKey",record);
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
        String sql  = "HsFeedbackDao.selectFeedbackListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsFeedbackDao.selectFeedbackListByCondition";
        }
        return this.executeSql(condition,sql);
    }

}
