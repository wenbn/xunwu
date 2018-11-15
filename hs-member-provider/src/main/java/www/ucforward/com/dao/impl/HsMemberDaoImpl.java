package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMemberDao;
import www.ucforward.com.entity.HsMember;

import java.util.List;
import java.util.Map;

@Repository("hsMemberDao")
public class HsMemberDaoImpl extends BaseDao implements HsMemberDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMemberDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsMember record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMember record) {
        return this.getSqlSession().delete("HsMemberDao.insertSelective" ,record);
    }

    @Override
    public HsMember selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMemberDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMember record) {
        return this.getSqlSession().update("HsMemberDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsMember record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 查询会员信息
     * @param queryFilter
     * @return
     */
    @Override
    public HsMember selectMemberByCondition(Map<Object, Object> queryFilter) {
        HsMember hsMember = null;
        List<HsMember> hsMembers = this.getSqlSession().selectList("HsMemberDao.selectMemberByCondition", queryFilter);
        if(hsMembers!=null && hsMembers.size()>0){
            hsMember = hsMembers.get(0);
        }
        return hsMember;
        //return this.getSqlSession().selectOne("HsMemberDao.selectMemberByCondition" ,queryFilter);
    }

    /**
     * 添加会员
     * @param member
     * @return
     */
    @Override
    public int addMember(HsMember member) {
        return this.getSqlSession().insert("HsMemberDao.addMember" ,member);
    }

    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        String sql = "HsMemberDao.selectMemberListByCondition";
        return this.executeSql(condition,sql);
    }

    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsMemberDao.selectCustomColumnNamesList");
    }
}