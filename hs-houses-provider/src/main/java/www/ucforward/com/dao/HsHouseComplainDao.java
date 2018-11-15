package www.ucforward.com.dao;

import org.springframework.stereotype.Repository;
import  www.ucforward.com.entity.HsHouseComplain;

import java.util.Map;

@Repository
public interface HsHouseComplainDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_complain
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_complain
     *
     * @mbg.generated
     */
    int insert(HsHouseComplain record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_complain
     *
     * @mbg.generated
     */
    int insertSelective(HsHouseComplain record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_complain
     *
     * @mbg.generated
     */
    HsHouseComplain selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_complain
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsHouseComplain record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_house_complain
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsHouseComplain record);

    Map<Object,Object> selectListByCondition(Map<Object, Object> condition, int returnType);
}