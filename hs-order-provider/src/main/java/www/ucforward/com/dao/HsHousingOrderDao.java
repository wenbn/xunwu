package www.ucforward.com.dao;

import www.ucforward.com.entity.HsHousingOrder;
import www.ucforward.com.entity.HsSystemOrderPool;
import www.ucforward.com.vo.ResultVo;

import java.util.List;
import java.util.Map;

public interface HsHousingOrderDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_housing_order
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_housing_order
     *
     * @mbg.generated
     */
    int insert(HsHousingOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_housing_order
     *
     * @mbg.generated
     */
    int insertSelective(HsHousingOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_housing_order
     *
     * @mbg.generated
     */
    HsHousingOrder selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_housing_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsHousingOrder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_housing_order
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsHousingOrder record);


    /**
     * 查询列表数据
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     * @throws Exception
     */
    Map<Object,Object> selectListByCondition(Map<Object, Object> condition, int returnType);

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition);

    /**
     * 批量修改数据
     * @param data
     * @return
     */
    int batchUpdate(List<HsHousingOrder> data);

    /**
     * 获取订单状态 字典表dict_order_status
     * @return
     */
    List<Map<Object,Object>> getOrderStatus();

    /**
     * 根据条件更新
     * @param condition
     * @return
     */
    int updateByCondition(Map<Object,Object> condition);

}