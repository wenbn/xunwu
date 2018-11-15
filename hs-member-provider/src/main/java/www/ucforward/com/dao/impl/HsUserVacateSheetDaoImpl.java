package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsUserVacateSheetDao;
import www.ucforward.com.entity.HsUserVacateSheet;

import java.util.List;
import java.util.Map;

@Repository("hsUserVacateSheetDao")
public class HsUserVacateSheetDaoImpl extends BaseDao implements HsUserVacateSheetDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsUserVacateSheetDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsUserVacateSheet record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsUserVacateSheet record) {
        return this.getSqlSession().insert("HsUserVacateSheetDao.insertSelective" ,record);
    }

    @Override
    public HsUserVacateSheet selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsUserVacateSheetDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsUserVacateSheet record) {
        return this.getSqlSession().update("HsUserVacateSheetDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsUserVacateSheet record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsUserVacateSheetDao.selectCustomColumnNamesList");
    }

    /**
     * 批量插入日志
     * @param data
     * @return
     */
    @Override
    public int batchInsert(List<HsUserVacateSheet> data) {
        return this.getSqlSession().insert("HsUserVacateSheetDao.batchInsert" ,data);
    }

    /**
     * 清除当前排班信息
     * @param condition
     * @return
     */
    @Override
    public int clearAttendanceCurrentMonth(Map<Object, Object> condition) {
        return this.getSqlSession().delete("HsUserVacateSheetDao.clearAttendanceCurrentMonth" ,condition);
    }

    /**
     * 清除用户请假记录
     * @param condition
     * @return
     */
    @Override
    public int clearUserAttendance(Map<Object, Object> condition) {
        return this.getSqlSession().delete("HsUserVacateSheetDao.clearUserAttendance" ,condition);
    }
}