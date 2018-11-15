package www.ucforward.com.dao;


import www.ucforward.com.entity.HsMemberHousingBargain;

import java.util.List;
import java.util.Map;

public interface HsMemberHousingBargainDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_housing_bargain
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_housing_bargain
     *
     * @mbg.generated
     */
    int insert(HsMemberHousingBargain record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_housing_bargain
     *
     * @mbg.generated
     */
    int insertSelective(HsMemberHousingBargain record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_housing_bargain
     *
     * @mbg.generated
     */
    HsMemberHousingBargain selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_housing_bargain
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsMemberHousingBargain record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_housing_bargain
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsMemberHousingBargain record);

    /**
     * 查询列表数据
     * @param condition 查询条件
     * @param returnType 返回值类型 0 List<Map> 1 List<Entity>
     * @return
     */
    Map<Object,Object> selectListByCondition(Map<Object,Object> condition, int returnType);

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition);

    /**
     * 获取议价列表
     * @param condition
     * @return
     */
    List<Map<Object,Object>> getMyBargainList(Map<Object, Object> condition);

    /**
     * 根据条件修改数据
     * @param condition
     * @return
     */
    Integer updateCustomColumnByCondition(Map<Object,Object> condition);
}