package www.ucforward.com.dao;

import org.springframework.stereotype.Repository;
import www.ucforward.com.entity.HsSystemOrderPool;

import java.util.List;
import java.util.Map;

@Repository
public interface HsSystemOrderPoolDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_system_order_pool
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_system_order_pool
     *
     * @mbg.generated
     */
    int insert(HsSystemOrderPool record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_system_order_pool
     *
     * @mbg.generated
     */
    int insertSelective(HsSystemOrderPool record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_system_order_pool
     *
     * @mbg.generated
     */
    HsSystemOrderPool selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_system_order_pool
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsSystemOrderPool record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_system_order_pool
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsSystemOrderPool record);


    /**
     * 查询列表数据
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     * @throws Exception
     */
    Map<Object,Object> selectListByCondition(Map<Object, Object> condition, int returnType);

    /**
     * 批量修改数据
     * @param data
     * @return
     */
    int batchUpdate(List<HsSystemOrderPool> data);

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition);
}