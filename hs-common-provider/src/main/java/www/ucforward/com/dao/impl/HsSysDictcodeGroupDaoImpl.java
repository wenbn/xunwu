package www.ucforward.com.dao.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Repository;
import org.utils.StringUtil;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsSysDictcodeGroupDao;
import www.ucforward.com.entity.HsSysDictcodeGroup;

import java.util.List;
import java.util.Map;

@Repository("hsSysDictcodeGroupDao")
public class HsSysDictcodeGroupDaoImpl extends BaseDao implements HsSysDictcodeGroupDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsSysDictcodeGroupDao.deleteByPrimaryKey" ,id);
    }

    @Override
    public int insert(HsSysDictcodeGroup record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsSysDictcodeGroup record) {
        return this.getSqlSession().insert("HsSysDictcodeGroupDao.insertSelective" ,record);
    }

    @Override
    public HsSysDictcodeGroup selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsSysDictcodeGroupDao.selectByPrimaryKey" ,id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsSysDictcodeGroup record) {
        return this.getSqlSession().update("HsSysDictcodeGroupDao.updateByPrimaryKeySelective" ,record);
    }

    @Override
    public int updateByPrimaryKey(HsSysDictcodeGroup record) {
        return updateByPrimaryKeySelective(record);
    }




    /**
     *
     * @param condition
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     */
    @Override
    public Map<Object, Object> selectListByCondition(Map<Object, Object> condition, int returnType) {
        //默认查询ListMap
        String sql  = "HsSysDictcodeGroupDao.selectDictcodeGroupListMapByCondition";
        if(returnType!=0){//查询实体
            sql  = "HsSysDictcodeGroupDao.selectDictcodeGroupListByCondition";
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
        return executeSql(condition, "HsSysDictcodeGroupDao.selectCustomColumnNamesList");
    }
}