package www.ucforward.com.dao;

import www.ucforward.com.entity.HsHouseKeyCases;

import java.util.Map;

public interface HsHouseKeyCasesDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_key_cases
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_key_cases
     *
     * @mbg.generated
     */
    int insert(HsHouseKeyCases record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_key_cases
     *
     * @mbg.generated
     */
    int insertSelective(HsHouseKeyCases record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_key_cases
     *
     * @mbg.generated
     */
    HsHouseKeyCases selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_key_cases
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsHouseKeyCases record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_key_cases
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsHouseKeyCases record);

    /**
     * 添加房源钥匙
     * @param houseKeyCases
     * @return
     */
    int addHouseKeys(HsHouseKeyCases houseKeyCases);

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    Map<Object, Object> selectCustomColumnNamesList(Map<Object, Object> condition);

    //批量设置过期
    int batchUpdateExpire(Map<Object,Object> condition);

    /**
     * 判断房源二维码是否被扫
     * @param condition
     * @return
     */
    Map<Object,Object> checkKeyIsExpire(Map<Object, Object> condition);

    /**
     * 获取第一个拿钥匙的业务员
     * @param condition
     * @return
     */
    Map<Object,Object> selectFirstGetHouseKey(Map<Object, Object> condition);
}