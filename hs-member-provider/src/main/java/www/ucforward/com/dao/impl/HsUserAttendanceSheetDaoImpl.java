package www.ucforward.com.dao.impl;

import org.springframework.stereotype.Repository;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsUserAttendanceSheetDao;
import www.ucforward.com.entity.HsUserAttendanceSheet;
import www.ucforward.com.entity.HsUserVacateSheet;

import java.util.List;
import java.util.Map;

@Repository("hsUserAttendanceSheetDao")
public class HsUserAttendanceSheetDaoImpl extends BaseDao implements HsUserAttendanceSheetDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsUserAttendanceSheetDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsUserAttendanceSheet record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsUserAttendanceSheet record) {
        return this.getSqlSession().insert("HsUserAttendanceSheetDao.insertSelective" ,record);
    }

    @Override
    public HsUserAttendanceSheet selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsUserAttendanceSheetDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsUserAttendanceSheet record) {
        return this.getSqlSession().update("HsUserAttendanceSheetDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsUserAttendanceSheet record) {
        return updateByPrimaryKeySelective(record);
    }

    @Override
    public int selectToDayCount(Map<Object, Object> condition) {
        return this.getSqlSession().selectOne("HsUserAttendanceSheetDao.selectToDayCount" ,condition);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsUserAttendanceSheetDao.selectCustomColumnNamesList");
    }

    /**
     * 批量插入
     * @param data
     * @return
     */
    @Override
    public int batchInsert(List<HsUserAttendanceSheet> data) {
        return this.getSqlSession().insert("HsUserAttendanceSheetDao.batchInsert" ,data);
    }
}