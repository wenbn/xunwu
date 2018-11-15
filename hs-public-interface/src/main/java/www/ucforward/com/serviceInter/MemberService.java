package www.ucforward.com.serviceInter;

import org.springframework.stereotype.Service;
import www.ucforward.com.entity.*;
import www.ucforward.com.vo.ResultVo;

import java.util.List;
import java.util.Map;

/**
 * Author:wenbn
 * Date:2018/1/3
 * Description:
 */
@Service
public interface MemberService<T> {

    //所有库公用的方法 start
    ResultVo delete(Integer id, Object o) throws Exception;

    ResultVo insert(Object o) throws Exception;

    ResultVo select(Integer id, Object o) throws Exception;

    ResultVo select(String id, Object o) throws Exception;

    /**
     * 查询列表数据
     * @param o 查询的实体，用于控制查询的dao
     * @param condition 查询参数
     * @param returnType 返回值类型，0 List<Map>  1 list<Entity>
     * @return
     * @throws Exception
     */
    ResultVo selectList(Object o, Map<Object,Object> condition, int returnType) throws Exception;

    /**
     * 自定义查询列数据
     * @param condition 查询条件 List<String> columns
     * @return
     * @throws Exception
     */
    ResultVo selectCustomColumnNamesList(T t, Map<Object,Object> condition) throws Exception;

    ResultVo update(Object o) throws Exception;

    ResultVo batchInsert(Object o, Map<Object, Object> condition);

    ResultVo batchUpdate(Object o,Map<Object,Object> condition);

    //所有库公用的方法 end

    //根据条件查询用户信息
    HsSysUser selectHsSysUserByCondition(Map<Object, Object> condition);

    //获取系统用户权限
    List<HsSysPermission> selectPermissionListByUser(Map<Object, Object> condition);

    ResultVo login(Map<Object, Object> condition) throws Exception;

    //计算可用的业务员
    List<Map<Object,Object>> allotUsableUsers(Map<Object, Object> condition);

    /**
     * 获取外获业务员
     * @param condition
     * @return
     */
    List<Map<Object,Object>> selectOutsideUser(Map<Object, Object> condition);

    /**
     * 修改用户订单任务信息
     * @param condition
     * @return
     */
    ResultVo updateUserOrderTask(Map<Object,Object> condition);

    /**
     * 查询今日可看
     * @param condition
     * @return
     */
    ResultVo getToday2SeeHouses(Map<Object, Object> condition);

    /**
     * 查询用户角色
     * @param condition
     * @return
     */
    ResultVo selectUserRoles(Map<Object, Object> condition);

    /**
     * 添加用户角色关联
     * @param id
     * @param principal
     * @param roles
     * @return
     */
    ResultVo addUserRoleRef(Integer id, ActiveUser principal, String[] roles);
    /**
     * 查询个人绩效
     * @param condition
     * @return
     */
    ResultVo selectPersonalPerformance(Map<Object,Object> condition);

    /**
     * 根据订单池ID更新预约看房信息
     * @param condition
     * @return
     */
    ResultVo updateHousingApplicationByPoolId(Map<Object,Object> condition);

    /**
     * 查询业务员对应的单数
     * @param condition
     * @return
     */
    ResultVo selectGroupUserTasksByCondition(Map<Object, Object> condition);


    /**
     * 业务员打卡记录
     * @return
     */
    ResultVo clockIn(HsUserAttendanceSheet sheet);

    /**
     * 修改看房数据
     * @param resultMap
     * @return
     */
    ResultVo updateSeeHouseApply(Map<Object, Object> resultMap);

    /**
     * 获取议价列表
     * @param condition 业主或客户id
     * @return
     */
    ResultVo getMyBargainList(Map<Object, Object> condition);

    /**
     * 检查系统中是否存在账号或手机号码
     * @param condition
     * @return
     */
    int checkExistUserCodeOrMobile(Map<Object, Object> condition);

    //批量删除数据,只有关联表才做删除
    ResultVo batchDelete(T t, Map<Object, Object> condition);

    //查询用户所能访问的资源名称
    ResultVo showPermissions(Map<Object, Object> condition);

    //查询角色对应的权限
    ResultVo showRolePermissions(Map<Object, Object> condition,String roleId);

    /**
     * 查询所有权限
     * @return
     */
    List<Map<String,Object>> findAllPermission(Map<Object, Object> condition);

    //查询过期的任务最后在哪个业务员手里
    List<HsSystemUserOrderTasks> selectExpiredTasks(Map<Object, Object> condition);

    /**
     * 获取最后一条聊天信息
     * @param o
     * @param condition
     * @return
     */
    List<Map<String,Object>> getLastMsg(Object o,Map<Object, Object> condition);

    /**
     * 根据条件修改数据
     * @param o
     * @param condition
     * @return
     */
    Integer updateCustomColumnByCondition(Object o,Map<Object,Object> condition);

    //加载下级员工及排班信息
    ResultVo getSubordinatePosition(Map<Object, Object> condition);

    //加载下级员工
    List<Integer> loadSubordinates(Map<Object, Object> condition);

    //清除当月设置的排班数据
    ResultVo clearAttendance(Map<Object, Object> condition);

    //清除用户的请假记录
    ResultVo clearUserAttendance(Map<Object, Object> condition);



}
