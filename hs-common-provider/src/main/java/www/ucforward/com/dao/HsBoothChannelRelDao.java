package www.ucforward.com.dao;

import org.springframework.stereotype.Repository;
import www.ucforward.com.entity.HsBoothChannelRel;

import java.util.List;
import java.util.Map;
@Repository
public interface HsBoothChannelRelDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_booth_channel_rel
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_booth_channel_rel
     *
     * @mbg.generated
     */
    int insert(HsBoothChannelRel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_booth_channel_rel
     *
     * @mbg.generated
     */
    int insertSelective(HsBoothChannelRel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_booth_channel_rel
     *
     * @mbg.generated
     */
    HsBoothChannelRel selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_booth_channel_rel
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsBoothChannelRel record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_booth_channel_rel
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsBoothChannelRel record);

    /**
     * 自定义查询列
     * @param condition
     * @return
     */
    Map<Object,Object> selectCustomColumnNamesList(Map<Object, Object> condition);

    /**
     * 批量插入数据
     * @param data
     * @param <T>
     * @return
     */
    <T> int batchInsert(List<T> data);

    /**
     * 批量删除数据
     * @param condition
     * @return
     */
    int batchDelete(Map<Object, Object> condition);
}