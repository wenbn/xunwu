package www.ucforward.com.dao;

import www.ucforward.com.entity.HsSupportCity;

import java.util.Map;

public interface HsSupportCityDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_support_city
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_support_city
     *
     * @mbg.generated
     */
    int insert(HsSupportCity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_support_city
     *
     * @mbg.generated
     */
    int insertSelective(HsSupportCity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_support_city
     *
     * @mbg.generated
     */
    HsSupportCity selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_support_city
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsSupportCity record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_support_city
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsSupportCity record);

    /**
     *查询所有
     * @param condition
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     */
    Map<Object,Object> selectListByCondition(Map<Object, Object> condition, int returnType);


    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition);
}