package www.ucforward.com.dao;


import www.ucforward.com.entity.HsMsgSys;

import java.util.List;
import java.util.Map;

public interface HsMsgSysDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_msg_sys
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_msg_sys
     *
     * @mbg.generated
     */
    int insert(HsMsgSys record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_msg_sys
     *
     * @mbg.generated
     */
    int insertSelective(HsMsgSys record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_msg_sys
     *
     * @mbg.generated
     */
    HsMsgSys selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_msg_sys
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsMsgSys record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_msg_sys
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsMsgSys record);

    /**
     * 获取系统消息
     * @param condition
     *                  platform  用户平台 外部:1 外获:2 外看:3
     *                  client    推送客户端 0:所有用户 1:android 2:ios 3:个别用户
     *                  userName  用户名（手机号）
     *                  pageIndex 当前页数
     *                  pageSize  每页显示数量
     * @return
     */
    Map<Object, Object> getSysMsg(Map<Object,Object> condition);
}