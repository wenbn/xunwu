package www.ucforward.com.dao;

import www.ucforward.com.entity.HsSysUser;

public interface HsSysUserDao {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_sys_user
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_sys_user
     *
     * @mbg.generated
     */
    int insert(HsSysUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_sys_user
     *
     * @mbg.generated
     */
    int insertSelective(HsSysUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_sys_user
     *
     * @mbg.generated
     */
    HsSysUser selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_sys_user
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(HsSysUser record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table hs_sys_user
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(HsSysUser record);
}