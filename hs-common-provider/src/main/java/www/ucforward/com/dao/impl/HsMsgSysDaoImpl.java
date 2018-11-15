package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsMsgSysDao;
import www.ucforward.com.entity.HsMsgSys;

import java.util.List;
import java.util.Map;

/**
 * @Auther: lsq
 * @Date: 2018/9/6 11:08
 * @Description:
 */
@Repository("hsMsgSysDao")
public class HsMsgSysDaoImpl extends BaseDao implements HsMsgSysDao {
    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsMsgSysDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsMsgSys record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsMsgSys record) {
        return this.getSqlSession().insert("HsMsgSysDao.insertSelective" ,record);
    }

    @Override
    public HsMsgSys selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsMsgSysDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsMsgSys record) {
        return this.getSqlSession().update("HsMsgSysDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsMsgSys record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public Map<Object, Object> getSysMsg(Map<Object, Object> condition) {
        return executeSql(condition, "HsMsgSysDao.getSysMsg");
    }
}
