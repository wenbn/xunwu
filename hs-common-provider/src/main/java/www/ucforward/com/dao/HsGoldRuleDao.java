package www.ucforward.com.dao;

import www.ucforward.com.entity.HsGoldRule;

import java.util.Map;

public interface HsGoldRuleDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_gold_rule
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_gold_rule
     *
     * @mbg.generated
     */
    int insert(HsGoldRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_gold_rule
     *
     * @mbg.generated
     */
    int insertSelective(HsGoldRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_gold_rule
     *
     * @mbg.generated
     */
    HsGoldRule selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_gold_rule
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsGoldRule record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_gold_rule
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsGoldRule record);

    Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition);
}