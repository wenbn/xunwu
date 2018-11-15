package www.ucforward.com.dao;

import www.ucforward.com.entity.HsMemberPurchase;

import java.util.Map;

public interface HsMemberPurchaseDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_purchase
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_purchase
     *
     * @mbg.generated
     */
    int insert(HsMemberPurchase record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_purchase
     *
     * @mbg.generated
     */
    int insertSelective(HsMemberPurchase record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_purchase
     *
     * @mbg.generated
     */
    HsMemberPurchase selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_purchase
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsMemberPurchase record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member_purchase
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsMemberPurchase record);

    /**
     * 查询列表数据
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return Map<Object,Object> result  key data,pageInfo
     * @throws Exception
     */
    Map<Object,Object> selectListByCondition(Map<Object, Object> condition, int returnType);

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition);
}