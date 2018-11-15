package www.ucforward.com.dao;

import org.springframework.stereotype.Repository;
import www.ucforward.com.entity.HsMember;

import java.util.Map;

@Repository
public interface HsMemberDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member
     *
     * @mbg.generated
     */
    int insert(HsMember record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member
     *
     * @mbg.generated
     */
    int insertSelective(HsMember record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member
     *
     * @mbg.generated
     */
    HsMember selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsMember record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_member
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsMember record);

    HsMember selectMemberByCondition(Map<Object, Object> queryFilter);

    /**
     *
     * @param member
     * @return
     */
    int addMember(HsMember member);

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