package www.ucforward.com.dao;

import www.ucforward.com.entity.HsUserGoldLog;

import java.util.List;
import java.util.Map;

public interface HsUserGoldLogDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_user_gold_log
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_user_gold_log
     *
     * @mbg.generated
     */
    int insert(HsUserGoldLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_user_gold_log
     *
     * @mbg.generated
     */
    int insertSelective(HsUserGoldLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_user_gold_log
     *
     * @mbg.generated
     */
    HsUserGoldLog selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_user_gold_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsUserGoldLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_user_gold_log
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsUserGoldLog record);

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition);

    /**
     * 批量插入日志
     * @param data
     * @return
     */
    int batchInsert(List<HsUserGoldLog> data);
}