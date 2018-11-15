package www.ucforward.com.dao.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSysUserDao;
import www.ucforward.com.entity.HsSysUser;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository("hsSysUserDao")
public class HsSysUserDaoImpl extends BaseDao implements HsSysUserDao {


    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsSysUserDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSysUser hsSysUser) {
        return insertSelective(hsSysUser);
    }

    @Override
    public int insertSelective(HsSysUser hsSysUser) {
        return this.getSqlSession().insert("HsSysUserDao.insertSelective" ,hsSysUser);
    }

    @Override
    public HsSysUser selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsSysUserDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSysUser hsSysUser) {
        return this.getSqlSession().update("HsSysUserDao.updateByPrimaryKeySelective" ,hsSysUser);
    }

    @Override
    public int updateByPrimaryKey(HsSysUser hsSysUser) {
        return updateByPrimaryKeySelective(hsSysUser);
    }

    /**
     *  根据条件查询用户信息
     * @param condition
     * @return
     */
    @Override
    public HsSysUser selectHsSysUserByCondition(Map<Object, Object> condition) {
        return this.getSqlSession().selectOne("HsSysUserDao.selectHsSysUserByCondition" ,condition);
    }

    /**
     * 查询用户角色
     * @param condition
     * @return
     */
    @Override
    public List<Map<Object, Object>> loadUserRoles(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsSysUserDao.loadUserRoles" ,condition);
    }

    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        return this.executeSql(condition,"HsSysUserDao.selectHsSysUserListByCondition");
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsSysUserDao.selectCustomColumnNamesList");
    }

    /**
     * 检查系统中是否存在账号或手机号码
     * @param condition
     * @return
     */
    @Override
    public int checkExistUserCodeOrMobile(Map<Object, Object> condition) {
        return this.getSqlSession().selectOne("HsSysUserDao.checkExistUserCodeOrMobile" ,condition);
    }

    /**
     * 批量修改用户
     * @param list
     * @return
     */
    @Override
    public int batchUpdate(List<HsSysUser> list) {
        return this.getSqlSession().update("HsSysUserDao.batchUpdate" ,list);
    }

    /**
     * 查询角色下的子员工
     * @param condition
     * @return
     */
    @Override
    public List<Map<Object, Object>> getSubordinatePositionByRoleId(Map<Object, Object> condition) {
        return this.getSqlSession().selectList("HsSysUserDao.getSubordinatePositionByRoleId" ,condition);
    }
}