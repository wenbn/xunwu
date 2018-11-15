package www.ucforward.com.dao.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Repository;
import org.utils.StringUtil;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSysDictcodeItemDao;
import www.ucforward.com.entity.HsSysDictcodeItem;

import java.util.List;
import java.util.Map;

@Repository("hsSysDictcodeItemDao")
public class HsSysDictcodeItemDaoImpl extends BaseDao implements HsSysDictcodeItemDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsSysDictcodeItemDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSysDictcodeItem record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsSysDictcodeItem record) {
        return this.getSqlSession().insert("HsSysDictcodeItemDao.insertSelective" ,record);
    }

    @Override
    public HsSysDictcodeItem selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsSysDictcodeItemDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSysDictcodeItem record) {
        return this.getSqlSession().update("HsSysDictcodeItemDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsSysDictcodeItem record) {
        return updateByPrimaryKeySelective(record);
    }

    //查询所有
    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        //默认查询ListMap
        String sql  = "HsSysDictcodeItemDao.selectDictcodeItemListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsSysDictcodeItemDao.selectDictcodeItemListByCondition";
        }
        return executeSql(condition,sql);
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsSysDictcodeItemDao.selectCustomColumnNamesList");
    }
}