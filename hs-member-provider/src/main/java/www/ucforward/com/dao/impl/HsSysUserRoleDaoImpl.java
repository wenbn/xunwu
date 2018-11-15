package www.ucforward.com.dao.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSysUserRoleDao;
import www.ucforward.com.entity.HsSysUserRole;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("hsSysUserRoleDao")
public class HsSysUserRoleDaoImpl extends BaseDao implements HsSysUserRoleDao {

    @Override
    public int deleteByPrimaryKey(String id) {
        return this.getSqlSession().delete("HsSysUserRoleDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSysUserRole record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsSysUserRole record) {
        return this.getSqlSession().insert("HsSysUserRoleDao.insertSelective" ,record);
    }

    @Override
    public HsSysUserRole selectByPrimaryKey(String id) {
        return this.getSqlSession().selectOne("HsSysUserRoleDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSysUserRole record) {
        return this.getSqlSession().update("HsSysUserRoleDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsSysUserRole record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 批量删除数据
     * @param condition
     * @return
     */
    @Override
    public int batchDelete(Map<Object, Object> condition) {
        return this.getSqlSession().delete("HsSysUserRoleDao.batchDelete" ,condition);
    }

    /**
     * 批量新增数据
     * @param data
     * @return
     */
    @Override
    public int batchInsert(List<HsSysUserRole> data) {
        return this.getSqlSession().insert("HsSysUserRoleDao.batchInsert" ,data);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsSysUserRoleDao.selectCustomColumnNamesList");
    }
}