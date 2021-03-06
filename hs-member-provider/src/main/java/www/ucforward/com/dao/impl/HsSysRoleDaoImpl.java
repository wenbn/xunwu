package www.ucforward.com.dao.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSysRoleDao;
import www.ucforward.com.entity.HsSysRole;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("hsSysRoleDao")
public class HsSysRoleDaoImpl extends BaseDao implements HsSysRoleDao {


    @Override
    public int deleteByPrimaryKey(String id) {
        return this.getSqlSession().delete("HsSysRoleDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSysRole hsSysRole) {
        return insertSelective(hsSysRole);
    }

    @Override
    public int insertSelective(HsSysRole hsSysRole) {
        return this.getSqlSession().insert("HsSysRoleDao.insertSelective" ,hsSysRole);
    }

    @Override
    public HsSysRole selectByPrimaryKey(String id) {
        return this.getSqlSession().selectOne("HsSysRoleDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSysRole hsSysRole) {
        return this.getSqlSession().update("HsSysRoleDao.updateByPrimaryKeySelective" ,hsSysRole);
    }

    @Override
    public int updateByPrimaryKey(HsSysRole hsSysRole) {
        return updateByPrimaryKeySelective(hsSysRole);
    }

    /**
     * 查询用户角色
     * @param condition
     * @return
     */
    @Override
    public List<Map<Object, Object>> selectRolesByUser(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsSysRoleDao.selectRolesByUser" ,condition);
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
        String sql  = "HsSysRoleDao.selectHsSysRoleListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsSysRoleDao.selectHsSysRoleListByCondition";
        }
        return this.executeSql(condition,sql);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsSysRoleDao.selectCustomColumnNamesList");
    }
}