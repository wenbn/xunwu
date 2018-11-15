package www.ucforward.com.dao.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSysRolePermissionDao;
import www.ucforward.com.entity.HsSysRolePermission;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("hsSysRolePermissionDao")
public class HsSysRolePermissionDaoImpl extends BaseDao implements HsSysRolePermissionDao {

    @Override
    public int deleteByPrimaryKey(String id) {
        return this.getSqlSession().delete("HsSysRolePermissionDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSysRolePermission record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsSysRolePermission record) {
        return this.getSqlSession().insert("HsSysRolePermissionDao.insertSelective" ,record);
    }

    @Override
    public HsSysRolePermission selectByPrimaryKey(String id) {
        return this.getSqlSession().selectOne("HsSysRolePermissionDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSysRolePermission record) {
        return this.getSqlSession().update("HsSysRolePermissionDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsSysRolePermission record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsSysRolePermissionDao.selectCustomColumnNamesList");
    }

    /**
     * 批量删除数据
     * @param condition
     * @return
     */
    @Override
    public int batchDelete(Map<Object, Object> condition) {
        return this.getSqlSession().delete("HsSysRolePermissionDao.batchDelete" ,condition);
    }

    /**
     * 批量插入数据
     * @param data
     * @return
     */
    @Override
    public int batchInsert(List<HsSysRolePermission> data) {
        return this.getSqlSession().insert("HsSysRolePermissionDao.batchInsert" ,data);
    }

    /**
     * 查询角色
     * @param condition
     * @return
     */
    @Override
    public List<Map<Object, Object>> selectRolePermission(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsSysRolePermissionDao.selectRolePermission" ,condition);
    }
}
