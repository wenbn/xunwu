package www.ucforward.com.dao.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.stereotype.Repository;
import org.utils.StringUtil;
import www.ucforward.com.dao.BaseDao;
import www.ucforward.com.dao.HsHouseKeyCasesDao;
import www.ucforward.com.entity.HsHouseKeyCases;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository("hsHouseKeyCasesDao")
public class HsHouseKeyCasesDaoImpl extends BaseDao implements HsHouseKeyCasesDao {

    @Override
    public int deleteByPrimaryKey(Integer id) {
        return this.getSqlSession().delete("HsHouseKeyCasesDao.deleteByPrimaryKey",id);
    }

    @Override
    public int insert(HsHouseKeyCases record) {
        return insertSelective(record);
    }

    @Override
    public int insertSelective(HsHouseKeyCases record) {
        return this.getSqlSession().insert("HsHouseKeyCasesDao.insertSelective",record);
    }

    @Override
    public HsHouseKeyCases selectByPrimaryKey(Integer id) {
        return this.getSqlSession().selectOne("HsHouseKeyCasesDao.selectByPrimaryKey",id);
    }

    @Override
    public int updateByPrimaryKeySelective(HsHouseKeyCases record) {
        return this.getSqlSession().update("HsHouseKeyCasesDao.updateByPrimaryKeySelective",record);
    }

    @Override
    public int updateByPrimaryKey(HsHouseKeyCases record) {
        return updateByPrimaryKeySelective(record);
    }

    /**
     * 添加房源钥匙
     * @param houseKeyCases
     * @return
     */
    @Override
    public int addHouseKeys(HsHouseKeyCases houseKeyCases) {
        int state = -1;
        //查询条件
        Map<Object,Object> condition = Maps.newHashMap();
        condition.put("houseId",houseKeyCases.getHouseId());//房源ID
        //condition.put("isExpire",0);//是否过期 0:未过期，1：已过期

        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        //查询条件
        queryColumn.clear();
        queryColumn.add("ID houseKeyId");//钥匙ID
        queryColumn.add("IS_EXPIRE isExpire");//是否过期 0:未过期，1：已过期
        condition.put("queryColumn",queryColumn);
        //查询房源是否存在钥匙
        Map<Object,Object> result = selectCustomColumnNamesList(condition);
        List<Map<Object,Object>> keysList = (List<Map<Object, Object>>) result.get("data");
        if( keysList!= null && keysList.size()>0 ){
            if(houseKeyCases.getMemberId() !=null && houseKeyCases.getMemberId() != -1){//业主创建钥匙，只能创建一次
                return -2;//已经存在钥匙
            }
            //未过期的房源信息
            List<Integer> unExpireIds = Lists.newArrayList();
            for (Map<Object, Object> key : keysList) {
                //将未过期的数据修改成为过期
                if(StringUtil.getAsInt(StringUtil.trim(key.get("isExpire")))==0){
                    unExpireIds.add(StringUtil.getAsInt(StringUtil.trim(key.get("houseKeyId"))));
                }
            }
            if(unExpireIds.size()>0){
                condition.clear();
                condition.put("isExpire",1);
                condition.put("updateTime",new Date());
                condition.put("ids",unExpireIds);
                state = batchUpdateExpire(condition);//修改成过期的
            }
        }
        //添加过房源钥匙
        state = insertSelective(houseKeyCases);
        if(state>0){
            state = 0;
        }
        return state;
    }

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition) {
        return executeSql(condition, "HsHouseKeyCasesDao.selectCustomColumnNamesList");
    }

    /**
     * 批量设置过期
     * @param condition
     * @return
     */
    @Override
    public int batchUpdateExpire(Map<Object, Object> condition) {
        return this.getSqlSession().update("HsHouseKeyCasesDao.batchUpdateExpire",condition);
    }

    /**
     * 判断房源二维码是否被扫
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> checkKeyIsExpire(Map<Object, Object> condition) {
        return this.getSqlSession().selectOne("HsHouseKeyCasesDao.checkKeyIsExpire",condition);
    }

    /**
     * 获取第一个拿钥匙的业务员
     * @param condition
     * @return
     */
    @Override
    public Map<Object, Object> selectFirstGetHouseKey(Map<Object, Object> condition) {
        return this.getSqlSession().selectOne("HsHouseKeyCasesDao.selectFirstGetHouseKey",condition);
    }
}