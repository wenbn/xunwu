package www.ucforward.com.manager.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.utils.DateUtils;
import org.utils.StringUtil;
import www.ucforward.com.constants.RedisConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.entity.*;
import www.ucforward.com.manager.IMemberManager;
import www.ucforward.com.serviceInter.HousesService;
import www.ucforward.com.serviceInter.MemberService;
import www.ucforward.com.serviceInter.OrderService;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/8/21
 */
@Service
public class IMemberManagerImpl implements IMemberManager {

    @Value("${PASSWORD_HASHITERATIONS}")
    private String PASSWORD_HASHITERATIONS;

    // 日志记录器
    private static Logger logger = LoggerFactory.getLogger(ICommonManagerImpl.class);

    @Resource
    private MemberService memberService;
    @Resource
    private OrderService orderService;
    @Resource
    private HousesService housesService;

    /**
     * 获取会员列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getMemberList(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID memberId");//主键ID
        queryColumn.add("MEMBER_CODE memberCode");//会员编号
        queryColumn.add("NICKNAME nickname");//会员昵称
        queryColumn.add("AREA_CODE areaCode");//电话地区号
        queryColumn.add("MEMBER_MOBLE memberMoble");//手机号
        queryColumn.add("MEMBER_LOGO memberLogo");//个人头像
        queryColumn.add("CITY city");//城市
        queryColumn.add("COMMUNITY community");//社区
        queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
        queryColumn.add("ADDRESS address");//会员地址
        queryColumn.add("SEX sex");//会员地址
        queryColumn.add("STATE state");//会员状态(0:停用,1:启用,2:已删除)默认为1
        queryColumn.add("LAST_LOGIN_TIME lastLoginTime");//最后登录时间
        //queryColumn.add("GOLD gold");//积分帐户,默认为0
        queryColumn.add("CREATE_TIME createTime");//创建时间
        condition.put("queryColumn", queryColumn);
        try {
            vo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
        } catch (Exception e) {
            logger.warn("Remote call to getMemberList fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        queryColumn = null;
        condition = null;
        return vo;
    }

    /**
     * 获取会员详情
     * @param memberId
     * @return
     */
    @Override
    public ResultVo getMemberDetail(Integer memberId) {
        ResultVo vo = null;
        try {
            vo = memberService.select(memberId, new HsMember());
        } catch (Exception e) {
            logger.warn("Remote call to getMemberDetail fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改会员
     * @param member
     * @return
     */
    @Override
    public ResultVo updateMember(HsMember member) {
        ResultVo vo = null;
        try {
            vo = memberService.select(member.getId(), member);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                return vo;
            }
            HsMember queryMember = (HsMember) vo.getDataSet();
            if(queryMember== null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"会员信息不存在");
            }
            vo = memberService.update(member);
        } catch (Exception e) {
            logger.warn("Remote call to updateMember fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增会员
     * @param member
     * @return
     */
    @Override
    public ResultVo addMember(HsMember member) {
        ResultVo vo = null;
        try {
            String memberMoble = member.getMemberMoble();
            String memberCode = SystemNumberUtil.createCode(ResultConstant.BUS_MEMBER_CODE_VALUE);
//            String areaCode = member.getAreaCode();
            //自定义查询列名
            Map<Object,Object> condition = Maps.newHashMap();
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID memberId");//主键ID
            queryColumn.add("MEMBER_CODE memberCode");//会员编号
            queryColumn.add("NICKNAME nickname");//会员昵称
            queryColumn.add("AREA_CODE areaCode");//电话地区号
            queryColumn.add("MEMBER_MOBLE memberMoble");//手机号
            condition.put("queryColumn", queryColumn);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                return vo;
            }
            List<Map<Object,Object>> members = (List<Map<Object,Object>>) vo.getDataSet();
            if(members== null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            for (Map<Object,Object> _member : members) {
                if(_member.containsValue(memberMoble)){//如果存在手机号 直接返回
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"手机号已存在");
                }
                if(_member.containsValue(memberCode)){//如果存在会员编号 直接返回
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"会员编号已存在");
                }
            }
            member.setMemberCode(memberCode);
            vo = memberService.insert(member);
        } catch (Exception e) {
            logger.warn("Remote call to addMember fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取内部用户列表
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getUserList(Map<Object, Object> condition) {
        ResultVo vo = null;
        //自定义查询列名
        List<String> queryColumn = new ArrayList<>();
        queryColumn.add("ID userId");//主键ID
        queryColumn.add("USERCODE usercode");//账号
        queryColumn.add("USERNAME username");//姓名
        queryColumn.add("USER_LOGO userLogo");//个人头像
        queryColumn.add("MOBILE mobile");//手机号
        queryColumn.add("LOCKED locked");//账号是否锁( 0未锁定 , 1：锁定)
        queryColumn.add("CITY city");//城市
        queryColumn.add("COMMUNITY community");//社区
        queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
        queryColumn.add("GOLD gold");//积分帐户,默认为0
        queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
        queryColumn.add("CREATE_TIME createTime");//创建时间
        condition.put("queryColumn", queryColumn);
        try {
            vo = memberService.selectCustomColumnNamesList(HsSysUser.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                return vo;
            }
            List<Map<Object,Object>> userLists = (List<Map<Object, Object>>) vo.getDataSet();
            if(userLists == null){
                return vo;
            }
            List<Integer> userIds = Lists.newArrayListWithCapacity(userLists.size());
            userLists.forEach(user -> {
                userIds.add(StringUtil.getAsInt(StringUtil.trim(user.get("userId"))));

            });
            condition.clear();
            condition.put("userIds",userIds);
            ResultVo resultVo = memberService.selectUserRoles(condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != resultVo.getResult()){
                return resultVo;
            }
            List<Map<Object,Object>> userRoles = (List<Map<Object, Object>>) resultVo.getDataSet();
            for (Map<Object, Object> user : userLists) {
                int userId = StringUtil.getAsInt(StringUtil.trim(user.get("userId")));
                for (Map<Object, Object> userRole : userRoles) {
                    int _userId = StringUtil.getAsInt(StringUtil.trim(userRole.get("userId")));
                    if(userId == _userId){
                        user.putAll(userRole);
                    }
                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to getUserList fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 获取内部用户详情
     *
     * @param userId
     * @return
     */
    @Override
    public ResultVo getUserDetail(Integer userId) {
        ResultVo vo = null;
        HashMap<Object, Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID userId");//主键ID
            queryColumn.add("USERCODE usercode");//账号
            queryColumn.add("USERNAME username");//姓名
            queryColumn.add("USER_LOGO userLogo");//个人头像
            queryColumn.add("MOBILE mobile");//手机号
            queryColumn.add("LOCKED locked");//账号是否锁( 0未锁定 , 1：锁定)
            queryColumn.add("CITY city");//城市
            queryColumn.add("COMMUNITY community");//社区
            queryColumn.add("SUB_COMMUNITY subCommunity");//子社区
            queryColumn.add("GOLD gold");//积分帐户,默认为0
            queryColumn.add("CREATE_TIME createTime");//创建时间
            condition.put("queryColumn", queryColumn);
            condition.put("id",userId);
            vo = memberService.selectCustomColumnNamesList(HsSysUser.class,condition);
            if(vo.getDataSet()!=null){
                List<Map<Object,Object>> userList = (List<Map<Object,Object>>)vo.getDataSet();
                Map<Object,Object> user  = userList.get(0);
                condition.clear();
                condition.put("userId",userId);
                ResultVo resultVo = memberService.selectUserRoles(condition);
                if(ResultConstant.SYS_REQUIRED_SUCCESS != resultVo.getResult()){
                    return resultVo;
                }
                List<Map<Object,Object>> userRoles  = (List<Map<Object, Object>>) resultVo.getDataSet();
                if(userRoles!=null && userRoles.size()>0){
                    String userLogo = StringUtil.trim(user.get("userLogo"));
                    if(StringUtil.hasText(userLogo)){
                        user.put("userLogo",ImageUtil.IMG_URL_PREFIX+userLogo);
                    }
                    user.putAll(userRoles.get(0));
//                }else{
//                    user.put("roleId","");
//                    user.put("roleName","");
                }
                vo.setDataSet(user);
            }
        } catch (Exception e) {
            logger.warn("Remote call to getUserDetail fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改内部用户
     * @param user
     * @param roles
     * @return
     */
    @Override
    public ResultVo updateUser(HsSysUser user,String[] roles) {
        ActiveUser principal = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        ResultVo vo = null;
        try {
            String password = user.getPassword();//判断密码是否为空
            String mobile = user.getMobile();//判断手机号码
            ResultVo x = checkUser(user);
            if (x != null) return x;
            if (StringUtil.hasText(password)) {//修改密码,默认6个1
                int salt = (int) ((Math.random() * 9 + 1) * 10000);
                SimpleHash md5 = new SimpleHash("MD5", password, salt + "", Integer.parseInt(PASSWORD_HASHITERATIONS));
                user.setPassword(md5.toString());//密码
                user.setSalt(StringUtil.trim(salt));
            }
            if (!StringUtil.hasText(mobile)) {
                //需要修改手机号
                Map<Object, Object> condition = Maps.newHashMap();
                condition.put("mobile", user.getMobile());//手机号码
                int count = memberService.checkExistUserCodeOrMobile(condition);//检查系统中是否存在账号或手机号码
                if (count > 0) {
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "已存在该手机号码");
                }
            }
            user.setUpdateTime(new Date());
            vo = memberService.update(user);
            if(roles!=null && roles.length>0){
                if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                    return vo;
                }
                ResultVo resultVo = memberService.addUserRoleRef(user.getId(), principal, roles);
                if(ResultConstant.SYS_REQUIRED_SUCCESS != resultVo.getResult()){
                    return resultVo;
                }
            }
        } catch (Exception e) {
            logger.warn("Remote call to updateUser fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增内部用户
     * @param user
     * @param roles 角色名
     * @return
     */
    @Override
    public ResultVo addUser(HsSysUser user,String[] roles) {
        ActiveUser principal = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        ResultVo vo = null;
        //自定义查询列名
        Map<Object, Object> condition = Maps.newHashMap();
        condition.put("usercode", user.getUsercode());//账号
        condition.put("mobile", user.getMobile());//手机号码
        try {
            int count = memberService.checkExistUserCodeOrMobile(condition);//检查系统中是否存在账号或手机号码
            if (count > 0) {
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "已存在该账号或者手机号码");
            }
            //修改密码,默认6个1
            int salt = (int) ((Math.random() * 9 + 1) * 10000);
            SimpleHash md5 = new SimpleHash("MD5", "111111", salt + "", Integer.parseInt(PASSWORD_HASHITERATIONS));
            //SimpleHash md5 = new SimpleHash("MD5", password, salt+"", Integer.parseInt(PASSWORD_HASHITERATIONS));
            user.setPassword(md5.toString());//密码
            user.setSalt(StringUtil.trim(salt));
            user.setCreateTime(new Date());
            vo = memberService.insert(user);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != vo.getResult()){
                return vo;
            }
            user = (HsSysUser) vo.getDataSet();
            ResultVo resultVo = memberService.addUserRoleRef(user.getId(), principal, roles);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != resultVo.getResult()){
                memberService.delete(user.getId(),user);
                return resultVo;
            }
        } catch (Exception e) {
            logger.warn("Remote call to addUser fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 检查用户是否正常
     * @param user
     * @return
     * @throws Exception
     */
    private ResultVo checkUser(HsSysUser user) throws Exception {
        ResultVo vo;
        vo = memberService.select(user.getId(), user);//查询用户是否存在，或者锁定
        if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
            return vo;
        }
        HsSysUser _user = (HsSysUser) vo.getDataSet();
        if(_user == null){
            return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"账号不存在");
        }
        Integer locked = _user.getLocked();//账号是否锁( 0未锁定 , 1：锁定)
        Integer isDel = _user.getIsDel();//是否删除( 0：未删除， 1:删除)
        if(locked == 1 || isDel == 1){
            return ResultVo.error(ResultConstant.USER_IS_LOCKED_OR_DELETE,ResultConstant.USER_IS_LOCKED_OR_DELETE_VALUE);
        }
        return null;
    }

    /**
     * 修改密码
     * @param userid
     * @param oldPassword
     * @param password
     * @return
     */
    @Override
    public ResultVo updatePwd(Integer userid, String oldPassword, String password) {
        ResultVo vo = null;
        //自定义查询列名
        Map<Object, Object> condition = Maps.newHashMap();
        try {
            vo = memberService.select(userid, new HsSysUser());//查询用户是否存在，或者锁定
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                return vo;
            }
            HsSysUser user = (HsSysUser) vo.getDataSet();
            if(user == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"账号不存在");
            }
            Integer locked = user.getLocked();//账号是否锁( 0未锁定 , 1：锁定)
            Integer isDel = user.getIsDel();//是否删除( 0：未删除， 1:删除)
            if(locked == 1 || isDel == 1){
                return ResultVo.error(ResultConstant.USER_IS_LOCKED_OR_DELETE,ResultConstant.USER_IS_LOCKED_OR_DELETE_VALUE);
            }
            String oldPassword1 = user.getPassword();
            int hashiterations = StringUtil.getAsInt(PASSWORD_HASHITERATIONS);
            SimpleHash oldMd5 = new SimpleHash("MD5", oldPassword, user.getSalt() + "",hashiterations);
            if(!oldPassword1.equals(oldMd5.toString())){//原密码错误
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"原密码错误");
            }
            int salt = (int) ((Math.random() * 9 + 1) * 10000);
            SimpleHash md5 = new SimpleHash("MD5", password, salt + "", hashiterations);
            user.setPassword(md5.toString());//密码
            user.setSalt(StringUtil.trim(salt));
            vo = memberService.update(user);
        } catch (Exception e) {
            logger.warn("Remote call to addUser fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 校验密码
     * @param userid
     * @param password
     * @return
     */
    @Override
    public ResultVo checkPwd(Integer userid, String password) {
        ResultVo vo = null;
        try {
            vo = memberService.select(userid, new HsSysUser());//查询用户是否存在，或者锁定
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                return vo;
            }
            HsSysUser user = (HsSysUser) vo.getDataSet();
            if(user == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"账号不存在");
            }
            Integer locked = user.getLocked();//账号是否锁( 0未锁定 , 1：锁定)
            Integer isDel = user.getIsDel();//是否删除( 0：未删除， 1:删除)
            if(locked == 1 || isDel == 1){
                return ResultVo.error(ResultConstant.USER_IS_LOCKED_OR_DELETE,ResultConstant.USER_IS_LOCKED_OR_DELETE_VALUE);
            }
            String oldPassword = user.getPassword();
            int hashiterations = StringUtil.getAsInt(PASSWORD_HASHITERATIONS);
            SimpleHash oldMd5 = new SimpleHash("MD5", password, user.getSalt() + "",hashiterations);
            if(!oldPassword.equals(oldMd5.toString())){//原密码错误
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"原密码错误");
            }
            vo.setDataSet(null);
        } catch (Exception e) {
            logger.warn("Remote call to checkPwd fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 发送短信验证码
     * @param condition
     * @return
     */
    @Override
    public ResultVo sendSmsValidateCode(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            String nationCode = "86";//区号
            //String nationCode = "971";//区号
            String mobile = StringUtil.trim(condition.get("mobile")); //手机号
            String ip = StringUtil.trim(condition.get("ip")); //ip地址
            int randomCode = (int) ((Math.random()*9+1)*100000); //随机数
            int templateId = 165947; //默认为国际短信模板
            String smsSign = "Hi Sandy"; //签名
            String [] phoneNumbers = {mobile}; //手机号
            String[] params = {randomCode+""}; //参数
            if("86".equals(nationCode)){ //如果是中国，发送国内短信
                templateId = 165847; //短信模板
                smsSign = "三迪科技";
            }
            String resultStr = SendSmsUtil.sendSms(nationCode,phoneNumbers,templateId,params,smsSign,null,null);
            if(!"success".equals(resultStr)){
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return result;
            }
            String cacheKey = RedisConstant.SYS_USER_REGISTER_IMG_CODE_KEY + nationCode + mobile + ip;
            RedisUtil.safeSet(cacheKey, randomCode+"", 60*15);//15分钟过期
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 修改手机号
     * @param condition
     * @return
     */
    @Override
    public ResultVo updatePhone(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            String phone = StringUtil.trim(condition.get("phone"));
            String validateCode = StringUtil.trim(condition.get("validateCode"));
            String ip = StringUtil.trim(condition.get("ip"));
            //校验短信验证码
            String cacheKey = RedisConstant.SYS_USER_REGISTER_IMG_CODE_KEY + "971"+phone+ip;
            String cacheValidateCode = "";
            if(!StringUtil.hasText(cacheKey)){
                return ResultVo.error(ResultConstant.SYS_IMG_CODE_IS_OVERDUE,ResultConstant.SYS_IMG_CODE_IS_OVERDUE_VALUE);
            }
            if(RedisUtil.isExistCache(cacheKey)){
                cacheValidateCode = RedisUtil.safeGet(cacheKey);
            }
            if(!cacheValidateCode.equalsIgnoreCase(validateCode)){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,"验证码错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }



    /**
     * 获取角色列表
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getRoleList(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            vo = memberService.selectList(new HsSysRole(), condition, 1);
        } catch (Exception e) {
            logger.warn("Remote call to getRolelList fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }

    /**
     * 获取角色详情
     *
     * @param roleId
     * @return
     */
    @Override
    public ResultVo getRoleDetail(String roleId) {
        ResultVo vo = null;
        try {
            vo = memberService.select(roleId, new HsSysRole());
        } catch (Exception e) {
            logger.warn("Remote call to getRoleDetail fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 修改角色信息
     *
     * @param role
     * @return
     */
    @Override
    public ResultVo updateRole(HsSysRole role) {
        ResultVo vo = null;
        try {
            String roleName = role.getRoleName();//获取角色名称,判断是否存在
            if (StringUtil.hasText(roleName)) {
                //自定义查询列名
                List<String> queryColumn = new ArrayList<>();
                Map<Object, Object> condition = Maps.newHashMap();
                queryColumn.add("ID roleId");//主键ID
                condition.put("roleName", roleName);
                condition.put("queryColumn", queryColumn);
                condition.put("isDel", 0);//是否删除0:不删除，1：删除
                vo = memberService.selectCustomColumnNamesList(HsSysRole.class, condition);
                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                List<Map<Object, Object>> roles = (List<Map<Object, Object>>) vo.getDataSet();
                if (roles != null && roles.size() > 0) {//角色名称已存在
                    if (roles.size() != 1 || !StringUtil.trim(roles.get(0).get("roleId")).equals(role.getId())) {
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "角色名称已存在");
                    }
                }
            }
            role.setUpdateTime(new Date());
            vo = memberService.update(role);
        } catch (Exception e) {
            logger.warn("Remote call to updateRole fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增角色信息
     *
     * @param role
     * @return
     */
    @Override
    public ResultVo addRole(HsSysRole role) {
        ResultVo vo = null;
        try {
            String roleName = role.getRoleName();//获取角色名称,判断是否存在
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            Map<Object, Object> condition = Maps.newHashMap();
            queryColumn.add("ID roleId");//主键ID
            condition.put("roleName", roleName);
            condition.put("queryColumn", queryColumn);
            vo = memberService.selectCustomColumnNamesList(HsSysRole.class, condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            List<Map<Object, Object>> roles = (List<Map<Object, Object>>) vo.getDataSet();
            if (roles != null && roles.size() > 0) {//角色名称已存在
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "角色名称已存在");
            }
            Date date = new Date();
            role.setId(MD5Utils.getPwd(DateUtils.getTime()));
            role.setCreateTime(date);
            role.setUpdateTime(date);
            vo = memberService.insert(role);
//            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
//                vo.setDataSet(null);
//            }
            queryColumn = null;
            condition = null;
            roles = null;
            date = null;
        } catch (Exception e) {
            logger.warn("Remote call to updateRole fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取用户对应的资源
     * @param condition
     * @return
     */
    @Override
    public ResultVo getUserPermissions(Map<Object, Object> condition) {
        ResultVo vo = null;
        try {
            vo = memberService.showPermissions(condition);
        } catch (Exception e) {
            logger.warn("Remote call to getUserPermissions fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 获取权限列表
     *
     * @param condition
     * @return
     */
    @Override
    public ResultVo getPermissionList(Map<Object, Object> condition) {
        ResultVo vo = new ResultVo();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            queryColumn.add("ID id");//角色权限表ID
            queryColumn.add("PERMISSION_NAME permissionName");//资源名称
            queryColumn.add("PERMISSION_TYPE permissionType");//资源类型：1：menu,2：button
            queryColumn.add("PERMISSION_URL permissionUrl");//访问url地址
            queryColumn.add("PERMISSION_CODE permissionCode");//权限代码字符串,权限标示，资源：操作：实例
            queryColumn.add("PARENT_ID parentId");//父结点id,默认为-1
            queryColumn.add("CURRENT_LEVEL currentLevel");//当前级
            queryColumn.add("SORT_NO sortNo");//排序号
            condition.put("queryColumn",queryColumn);
            condition.put("isForbidden",0);//是否禁用,0：启用，1禁用
            condition.put("isDel",0);//角色ID
            List<Map<String, Object>> perms =memberService.findAllPermission(condition);
            vo.setDataSet(perms);
        } catch (Exception e) {
            logger.warn("Remote call to getRolelList fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        condition = null;
        return vo;
    }


    /**
     * 获取权限详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getPermissionDetail(Integer id) {
        ResultVo vo = null;
        try {
            vo = memberService.select(id,new HsSysPermission());
        } catch (Exception e) {
            logger.warn("Remote call to getPermissionDetail fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 新增权限
     *
     * @param permission
     * @return
     */
    @Override
    public ResultVo addPermission(HsSysPermission permission) {
        ResultVo vo = null;
        try {
            String permissionName = permission.getPermissionName();//资源名称
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            Map<Object, Object> condition = Maps.newHashMap();
            queryColumn.add("ID permissionId");//主键ID
            condition.put("permissionName", permissionName);
            condition.put("isDel", 0);
            Integer parentId = permission.getParentId();
            if(parentId!=null){
                condition.put("parentId", parentId);
            }
            condition.put("isDel", 0);
            condition.put("queryColumn", queryColumn);
            vo = memberService.selectCustomColumnNamesList(HsSysPermission.class, condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            List<Map<Object, Object>> permisssion = (List<Map<Object, Object>>) vo.getDataSet();
            if (permisssion != null && permisssion.size() > 0) {//资源名称已存在
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "资源名称已存在");
            }
            Date date = new Date();
            permission.setCreateTime(date);
            permission.setUpdateTime(date);
            vo = memberService.insert(permission);
            if (vo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS) {
                vo.setDataSet(null);
            }
            queryColumn = null;
            condition = null;
            permission = null;
            date = null;
        } catch (Exception e) {
            logger.warn("Remote call to addPermission fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo ;
    }

    /**
     * 修改权限
     * @param permission
     * @return
     */
    @Override
    public ResultVo updatePermission(HsSysPermission permission) {
        ResultVo vo = null;
        try {
            String permissionName = permission.getPermissionName();//资源名称
            if (StringUtil.hasText(permissionName)) {
                //自定义查询列名
                List<String> queryColumn = new ArrayList<>();
                Map<Object, Object> condition = Maps.newHashMap();
                queryColumn.add("ID permissionId");//主键ID
                condition.put("permissionName", permissionName);
                condition.put("queryColumn", queryColumn);
                condition.put("isDel", 0);//是否删除0:不删除，1：删除
//                condition.put("id",permission.getId());
                vo = memberService.selectCustomColumnNamesList(HsSysPermission.class, condition);
                if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                    return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                }
                List<Map<Object, Object>> roles = (List<Map<Object, Object>>) vo.getDataSet();
                if (roles != null && roles.size() > 0) {//资源名称已存在
                    if (roles.size() != 1 || StringUtil.getAsInt(StringUtil.trim(roles.get(0).get("permissionId"))) != permission.getId()) {
                        return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "资源名称已存在");
                    }
                }
            }
            permission.setUpdateTime(new Date());
            vo = memberService.update(permission);
        } catch (Exception e) {
            logger.warn("Remote call to updatePermission fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 删除权限
     * @param permission
     * @return
     */
    @Override
    public ResultVo deletePermission(HsSysPermission permission) {
        ResultVo vo = null;
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            Map<Object, Object> condition = Maps.newHashMap();
            queryColumn.add("ID id");//主键ID
            queryColumn.add("IS_DEL isDel");//是否删除0:不删除，1：删除
            condition.put("queryColumn", queryColumn);
            condition.put("id", permission.getId());
            vo = memberService.selectCustomColumnNamesList(HsSysPermission.class, condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
            }
            List<Map<Object, Object>> permissions = (List<Map<Object, Object>>) vo.getDataSet();
            if (permissions == null && permissions.size() == 0) {//资源名称不存在
                return ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE, "资源名称不存在");
            }
            if(StringUtil.getAsInt(StringUtil.trim(permissions.get(0).get("isDel")))==1){
                return ResultVo.success();
            }
            permission.setUpdateTime(new Date());
            vo = memberService.update(permission);
        } catch (Exception e) {
            logger.warn("Remote call to updatePermission fails" + e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR, ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 为角色添加权限,先判断是否有对应的权限，如果已存在关联删除，不存在的直接删除
     * @param pids 权限IDs
     * @param roleId 角色ID
     * @return
     */
    @Override
    public ResultVo grantPermission(List<Integer> pids, String roleId) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID rolePermissionId");//角色权限表ID
            queryColumn.add("PERMISSION_ID permissionId");//权限id
            condition.put("queryColumn",queryColumn);
            condition.put("isForbidden",0);//是否禁用,0：启用，1禁用
            condition.put("roleId",roleId);//角色ID
            vo = memberService.selectCustomColumnNamesList(HsSysRolePermission.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            //获取该角色下已经绑定的权限信息
            List<Map<Object,Object>> bindingRolePermissions = (List<Map<Object, Object>>) vo.getDataSet();
            List<String> rolePermissionIds = Lists.newArrayList();//需要移除的权限信息
            if(bindingRolePermissions!=null && bindingRolePermissions.size()>0){
                for (Map<Object, Object> rolePermission : bindingRolePermissions) {//遍历已绑定的展位数,将已绑定的Id过滤掉
                    String rolePermissionId = StringUtil.trim(rolePermission.get("rolePermissionId"));
                    Integer permissionId = StringUtil.getAsInt(StringUtil.trim(rolePermission.get("permissionId")));
                    if(pids.contains(permissionId)){//如果包含已绑定的
                        pids.remove(permissionId);
                    }else{//添加到需要移除的权限信息
                        rolePermissionIds.add(rolePermissionId);
                    }
                }
            }
            if(rolePermissionIds!=null && rolePermissionIds.size()>0){
                //批量删除数据,只有关联表才做删除
                condition.clear();
                condition.put("ids",rolePermissionIds);
                ResultVo batchDeleteVo = memberService.batchDelete(HsSysRolePermission.class,condition);
            }

            if(pids!=null && pids.size()>0){
                List<HsSysRolePermission> perms = Lists.newArrayList();
                HsSysRolePermission perm = null;
                Date date =  new Date();
                for (Integer pid : pids) {//
                    perm =new HsSysRolePermission();
//                    perm.setId(MD5Utils.getPwd(DateUtils.getTime()));
                    perm.setId(RandomUtils.getUUID());
                    perm.setPermissionId(pid);
                    perm.setRoleId(roleId);
                    perm.setCreateTime(date);
                    perm.setIsForbidden(0);
                    perm.setCreateBy("-1");
                    perm.setUpdateBy("-1");
                    perms.add(perm);
                }
                //批量插入数据
                condition.clear();
                condition.put("data",perms);
                vo = memberService.batchInsert(new HsSysRolePermission(),condition);


                perms = null;
                perm = null;
                date = null;

            }
            condition =null;
            queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to grantPermission fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 显示角色对应的权限
     * @param roleId
     * @return
     */
    @Override
    public ResultVo showRolePermissions(String roleId) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            queryColumn.add("ID id");//角色权限表ID
            queryColumn.add("PERMISSION_NAME permissionName");//资源名称
            queryColumn.add("PERMISSION_TYPE permissionType");//资源类型：1：menu,2：button
            queryColumn.add("PERMISSION_URL permissionUrl");//访问url地址
            queryColumn.add("PARENT_ID parentId");//父结点id,默认为-1
            queryColumn.add("CURRENT_LEVEL currentLevel");//当前级
            queryColumn.add("SORT_NO sortNo");//排序号
            condition.put("queryColumn",queryColumn);
            condition.put("isForbidden",0);//是否禁用,0：启用，1禁用
            condition.put("isDel",0);//角色ID
            vo = memberService.showRolePermissions(condition,roleId);

        } catch (Exception e) {
            logger.warn("Remote call to grantPermission fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    /**
     * 为用户添加角色 先判断是否有对应的权限，如果已存在关联删除，不存在的直接删除
     * @param roleIdsList
     * @param userId
     * @return
     */
    @Override
    public ResultVo grantRole(List<String> roleIdsList, Integer userId) {
        ResultVo vo = null;
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //自定义查询列名
            List<String> queryColumn = new ArrayList<>();
            //查询该频道下已绑定的数据
            queryColumn.add("ID userRoleId");//用户角色表ID
            queryColumn.add("ROLE_ID roleId");//角色id
            condition.put("queryColumn",queryColumn);
            condition.put("userId",userId);//角色ID
            vo = memberService.selectCustomColumnNamesList(HsSysUserRole.class,condition);
            if (vo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS) {//查询成功
                return vo;
            }
            //获取该用户下已经绑定的角色信息
            List<Map<Object,Object>> bindingUserRoles = (List<Map<Object, Object>>) vo.getDataSet();
            List<String> roleIds = Lists.newArrayList();//需要移除的角色信息
            if(bindingUserRoles!=null && bindingUserRoles.size()>0){
                for (Map<Object, Object> userRole : bindingUserRoles) {//遍历已绑定的展位数,将已绑定的Id过滤掉
                    String userRoleId = StringUtil.trim(userRole.get("userRoleId"));//用户角色表ID
                    String roleId = StringUtil.trim(userRole.get("roleId"));//角色id
                    if(roleIdsList.contains(roleId)){//如果包含已绑定的
                        roleIdsList.remove(roleId);
                    }else{//添加到需要移除的权限信息
                        roleIds.add(userRoleId);
                    }
                }
            }
            if(roleIds!=null && roleIds.size()>0){
                //批量删除数据,只有关联表才做删除
                condition.clear();
                condition.put("ids",roleIds);
                ResultVo batchDeleteVo = memberService.batchDelete(HsSysUserRole.class,condition);
            }

            if(roleIdsList!=null && roleIdsList.size()>0){
                List<HsSysUserRole> userRoles = Lists.newArrayList();
                HsSysUserRole userRole = null;
                Date date =  new Date();
                for (String roleId : roleIdsList) {//
                    userRole =new HsSysUserRole();
                    userRole.setId(MD5Utils.getPwd(DateUtils.getTime()));
                    userRole.setRoleId(roleId);
                    userRole.setUserId(userId);
                    userRole.setCreateTime(date);
                    userRole.setCreateBy("-1");
                    userRole.setUpdateBy("-1");
                    userRoles.add(userRole);
                }
                //批量插入数据
                condition.clear();
                condition.put("data",userRoles);
                vo = memberService.batchInsert(new HsSysUserRole(),condition);

                userRoles = null;
                userRole = null;
                date = null;

            }
            condition =null;
            queryColumn =null;
        } catch (Exception e) {
            logger.warn("Remote call to grantPermission fails"+e);
            vo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return vo;
    }

    @Override
    public ResultVo addLoansApply(HsMemberFinancialLoansApply financialLoansApply) {
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = memberService.insert(financialLoansApply);
        }catch (Exception e){
            logger.warn("Remote call to addLoansApply fails"+e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    @Override
    public ResultVo getLoansApply(Map<Object,Object> condition){
        ResultVo resultVo = new ResultVo();
        try {
            ResultVo selectList = memberService.selectList(new HsMemberFinancialLoansApply(), condition, 0);
            if(selectList != null && selectList.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && selectList.getDataSet() != null){
                List<Map<Object, Object>> dataSet = (List<Map<Object, Object>>) selectList.getDataSet();
                Map<Object, Object> objectObjectMap = dataSet.get(0);
                resultVo.setDataSet(objectObjectMap);
            }
        }catch (Exception e){
            logger.warn("Remote call to getLoansApply fails"+e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    @Override
    public ResultVo getPurchaseApplyList(Map<Object,Object> condition){
        ResultVo resultVo = new ResultVo();
        List<Map<Object, Object>> resultList = new ArrayList<>();
        try {
            ResultVo applyResultVo = memberService.selectList(new HsMemberDirectPurchaseApply(), condition, 0);
            if(applyResultVo == null || applyResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || applyResultVo.getDataSet() == null){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return resultVo;
            }
            List<Map<Object, Object>> applyList = (List<Map<Object, Object>>) applyResultVo.getDataSet();
            //获取楼盘id
            List<String> buildingIds = applyList.stream().map(map -> StringUtil.trim(map.get("buildingId"))).collect(Collectors.toList());
            condition.clear();
            condition.put("ids",buildingIds);
            condition.put("isDel",0);
            ResultVo buildingResultVo = housesService.selectList(new HsHouseNewBuilding(), condition, 0);
            if(buildingResultVo != null && buildingResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && buildingResultVo.getDataSet() != null){
                List<Map<Object, Object>> buildingList = (List<Map<Object, Object>>) buildingResultVo.getDataSet();
                for (Map<Object, Object> map : applyList) {
                    String buildingId = StringUtil.trim(map.get("buildingId"));
                    for (Map<Object, Object> buildingMap : buildingList) {
                        String id = StringUtil.trim(buildingMap.get("id"));
                        if(id.equals(buildingId)){
                            String projectName = StringUtil.trim(buildingMap.get("projectName"));
                            double maxHouseRent = StringUtil.getDouble(StringUtil.trim(buildingMap.get("maxHouseRent")));
                            String city = StringUtil.trim(buildingMap.get("city"));
                            String community = StringUtil.trim(buildingMap.get("community"));
                            String subCommunity = StringUtil.trim(buildingMap.get("subCommunity"));
                            StringBuilder area = new StringBuilder();
                            area.append(city)
                                    .append(" ")
                                    .append(community)
                                    .append(" ")
                                    .append(subCommunity);
                            map.put("projectName",projectName);
                            map.put("area",area.toString());
                            map.put("maxHouseRent",maxHouseRent);
                            break;
                        }
                    }
                    resultList.add(map);
                }
            }
            resultVo.setDataSet(resultList);
            resultVo.setPageInfo(applyResultVo.getPageInfo());
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    @Override
    public ResultVo getPurchaseApplyDetails(Integer id) {
        ResultVo resultVo = new ResultVo();
        Map<String,Object> resultMap = new HashMap<>(10);
        try {
            resultVo = memberService.select(id, new HsMemberDirectPurchaseApply());
            if(resultVo == null && resultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() == null){
                resultVo.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                resultVo.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
                return resultVo;
            }
            HsMemberDirectPurchaseApply purchaseApply = (HsMemberDirectPurchaseApply) resultVo.getDataSet();
            Integer buildingId = purchaseApply.getBuildingId();
            Integer memberId = purchaseApply.getMemberId();
            resultMap.put("code",purchaseApply.getCode());
            resultMap.put("createTime",purchaseApply.getCreateTime());
            resultMap.put("status",purchaseApply.getStatus());

            //获取楼盘信息
            String projectName = "";
            String area = "";
            ResultVo buildingResultVo = housesService.select(buildingId, new HsHouseNewBuilding());
            if(buildingResultVo != null && buildingResultVo.getDataSet() != null && buildingResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                HsHouseNewBuilding building = (HsHouseNewBuilding) buildingResultVo.getDataSet();
                projectName = building.getProjectName();
                String city = building.getCity();
                String community = building.getCommunity();
                String subCommunity = building.getSubCommunity();
                area = city + " " + community + " " + subCommunity;
            }
            //获取用户申购信息
            //用户姓名
            String name = "";
            Map<Object,Object> condition = new HashMap<>(2);
            //自定义查询的列名
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("NAME name");
            queryColumn.add("FAMILY_NAME familyName");
            condition.put("queryColumn",queryColumn);
            condition.put("isDel",0);
            condition.put("memberId",memberId);
            ResultVo buildingMemberResultVo =housesService.selectCustomColumnNamesList(HsHouseNewBuildingMemberApply.class,condition);
            if(buildingMemberResultVo != null && buildingMemberResultVo.getDataSet() != null && buildingMemberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object,Object>> buildingMemberApplyList = (List<Map<Object,Object>>) buildingMemberResultVo.getDataSet();
                if(buildingMemberApplyList.size() > 0){
                    Map<Object, Object> buildingMemberApply = buildingMemberApplyList.get(0);
                    String familyName = StringUtil.trim(buildingMemberApply.get("familyName"));
                    String nameStr = StringUtil.trim(buildingMemberApply.get("name"));
                    name = familyName + nameStr;
                }
            }
            resultMap.put("projectName",projectName);
            resultMap.put("area",area);
            resultMap.put("name",name);
            resultVo.setDataSet(resultMap);
        }catch (Exception e){
            logger.warn("Remote call to getPurchaseApplyDetails fails"+e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取关单记录
     * @param condition
     * @return
     */
    @Override
    public ResultVo getUserCloseOrderRecordList(Map<Object,Object> condition) {
        ResultVo resultVo = null;
        List<String> queryColumn = Lists.newArrayList();
        try {
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            condition.clear();
            queryColumn.clear();
            queryColumn.add("ID taskId");//任务ID
            queryColumn.add("POOL_ID poolId");//订单池ID
            condition.put("queryColumn",queryColumn);
            condition.put("isDel",0);//删除
            condition.put("userId",userId);//用户ID
            condition.put("isFinished",4);//是否完成0:未完成，1：已完成（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭
            //根据订单池查询业务员任务
            resultVo = memberService.selectCustomColumnNamesList(HsSystemUserOrderTasks.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != resultVo.getResult()) {
                return resultVo;
            }
            List<Map<Object,Object>> tasksList = (List<Map<Object, Object>>) resultVo.getDataSet();
            if(tasksList == null ){
                return resultVo;
            }
            List<Integer> poolIds = Lists.newArrayListWithCapacity(tasksList.size());
            for (Map<Object, Object> task : tasksList) {
                poolIds.add(StringUtil.getAsInt(StringUtil.trim(task.get("poolId"))));
            }
            //自定义查询列名
            queryColumn.clear();
            condition.clear();
            queryColumn.add("ID poolId");//主键ID
            queryColumn.add("ORDER_CODE orderCode");//订单编码
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0外获订单->1-外看订单->2合同订单
            queryColumn.add("HOUSE_ID houseId");//房源ID
            queryColumn.add("IS_FINISHED isFinished");//是否完成0 :未派单，1：已派单（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭 5：已完成
            queryColumn.add("REMARK remark");//备注(说明)
            queryColumn.add("CREATE_TIME createTime");//创建时间
            queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
            queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace");//见面地点
            condition.put("queryColumn",queryColumn);
            if(poolIds.size() > 0){
                condition.put("poolIds",poolIds);
            }
            ResultVo orderVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != orderVo.getResult()){
                return orderVo;
            }
            List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) orderVo.getDataSet();
            if(orderList == null || orderList.size() < 1){
                return orderVo;
            }
            /**
             * 获取房源信息
             */
            List<Map<Object, Object>> houseList = new ArrayList<>();
            //房源id列表
            List<Integer> houseIds = orderList.stream().map(map -> StringUtil.getAsInt(StringUtil.trim(map.get("houseId")))).collect(Collectors.toList());
            if(houseIds.size() > 0){
                //获取房源信息
                condition.clear();
                condition.put("houseIds",houseIds);
                condition.put("isDel",0);
                ResultVo housesResultVo = housesService.selectList(new HsMainHouse(), condition, 0);
                if(housesResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && housesResultVo.getDataSet() != null){
                    houseList = (List<Map<Object, Object>>) housesResultVo.getDataSet();
                }
            }
            /**
             * 业主信息列表
             */
            List<HsMember> memberList = new ArrayList<>();
            //业主id列表
            List<Integer> memberIds = houseList.stream().map(house -> StringUtil.getAsInt(StringUtil.trim(house.get("memberId")))).collect(Collectors.toList());
            if(memberIds.size() > 0){
                //获取业主信息
                condition.clear();
                condition.put("memberIds",memberIds);
                condition.put("isDel",0);
                ResultVo memberResultVo = memberService.selectList(new HsMember(), condition, 0);
                if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                    memberList = (List<HsMember>) memberResultVo.getDataSet();
                }
            }
            /**
             * 封装信息
             */
            for (Map<Object, Object> orederMap : orderList) {
                //房源id
                int houseId = StringUtil.getAsInt(StringUtil.trim(orederMap.get("houseId")));
                //预约类型（0：出租，1：出售）
                Integer leaseType = null;
                //房源主图
                String houseMainImg = "";
                //业主id
                Integer memberId = -1;
                //业主电话
                String memberMobile = "";
                Optional<Map<Object, Object>> houseOptional = houseList.stream().filter(house -> StringUtil.getAsInt(StringUtil.trim(house.get("id"))) == houseId).findFirst();
                if(houseOptional.isPresent()){
                    //房源信息
                    Map<Object, Object> houseMap = houseOptional.get();
                    leaseType = StringUtil.getAsInt(StringUtil.trim(houseMap.get("leaseType")));
                    houseMainImg = ImageUtil.imgResultUrl(StringUtil.trim(houseMap.get("houseMainImg")));
                    memberId = StringUtil.getAsInt(StringUtil.trim(houseMap.get("memberId")));
                }

                Integer finalMemberId = memberId;
                Optional<HsMember> memberOptional = memberList.stream().filter(member -> member.getId().equals(finalMemberId)).findFirst();
                if(memberOptional.isPresent()){
                    //业主信息
                    HsMember memberMap = memberOptional.get();
                    memberMobile = StringUtil.trim(memberMap.getMemberMoble());
                }
                //封装房源、业主信息
                orederMap.put("leaseType",leaseType);
                orederMap.put("houseMainImg",houseMainImg);
                orederMap.put("memberMobile",memberMobile);
            }

            for (Map<Object, Object> task : tasksList) {
                int poolId = StringUtil.getAsInt(StringUtil.trim(task.get("poolId")));
                for (int i = orderList.size()-1 ;i>= 0 ;i--) {
                    Map<Object, Object> order = orderList.get(i);
                    int _poolId = StringUtil.getAsInt(StringUtil.trim(order.get("poolId")));
                    if(poolId == _poolId){
                        task.putAll(order);
                        orderList.remove(i);
                        break;
                    }
                }
            }
            resultVo.setDataSet(tasksList);
        }catch (Exception e){
            logger.warn("Remote call to getUserCloseOrderRecordList fails"+e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取会员关单详情
     * @param taskId
     * @return
     */
    @Override
    public ResultVo getUserCloseOrderRecordDetail(Integer taskId) {
        ResultVo resultVo = null;
        List<String> queryColumn = Lists.newArrayList();
        Map<Object,Object> condition = Maps.newHashMap();
        try {
            //根据订单池查询业务员任务
            resultVo = memberService.select(taskId,new HsSystemUserOrderTasks());
            if(ResultConstant.SYS_REQUIRED_SUCCESS != resultVo.getResult()) {
                return resultVo;
            }
            HsSystemUserOrderTasks tasks= (HsSystemUserOrderTasks) resultVo.getDataSet();
            if(tasks == null ){
                return resultVo;
            }
            List<Integer> poolIds = Lists.newArrayListWithCapacity(1);
            poolIds.add(StringUtil.getAsInt(StringUtil.trim(tasks.getPoolId())));

            Map<Object, Object> taskMap = MapClassUtil.beanToMap(tasks);
            //自定义查询列名
            queryColumn.clear();
            condition.clear();
            queryColumn.add("ID poolId");//主键ID
            queryColumn.add("ORDER_CODE orderCode");//订单编码
            queryColumn.add("ORDER_TYPE orderType");//订单类型 0外获订单->1-外看订单->2合同订单
            queryColumn.add("HOUSE_ID houseId");//主键ID
            queryColumn.add("IS_FINISHED isFinished");//是否完成0:未派单，1：已派单（用于控制系统是否自动派单）2：业主取消预约3：租客/买家取消预约，4：订单已关闭 5：已完成
            queryColumn.add("REMARK remark");//备注(说明)
            queryColumn.add("CREATE_TIME createTime");//创建时间
            queryColumn.add("ESTIMATED_TIME estimatedTime");//预计完成时间
            queryColumn.add("APPOINTMENT_MEET_PLACE appointmentMeetPlace");//见面地点
            condition.put("queryColumn",queryColumn);
            condition.put("poolIds",poolIds);
            ResultVo orderVo = orderService.selectCustomColumnNamesList(HsSystemOrderPool.class, condition);
            if(ResultConstant.SYS_REQUIRED_SUCCESS != orderVo.getResult()){
                return orderVo;
            }
            List<Map<Object,Object>> orderList = (List<Map<Object, Object>>) orderVo.getDataSet();
            if(orderList == null){
                return orderVo;
            }
            taskMap.putAll(orderList.get(0));
            resultVo.setDataSet(taskMap);
        }catch (Exception e){
            logger.warn("Remote call to getUserCloseOrderRecordDetail fails"+e);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 业务员查询考勤
     * @param condition
     * @return
     */
    @Override
    public ResultVo queryUserAttendances(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            int queryMonthsAgo = StringUtil.getAsInt(StringUtil.trim(condition.get("queryMonthsAgo")));
            //返回的结果集
            List<Map<Object,Object>> data = Lists.newArrayList();
            List<String> dayListOfMonths = DateUtil.getMonthFullDays(queryMonthsAgo);
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("POST_TIME postTime");//打卡时间
            condition.put("queryColumn",queryColumn);
            //查询业务员打卡记录
            result = memberService.selectCustomColumnNamesList(HsUserAttendanceSheet.class,condition);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return result;
            }
            List<Map<Object,Object>> attendanceList = (List<Map<Object, Object>>) result.getDataSet();

            //查询业务员当月请假记录
            queryColumn.clear();
            queryColumn.add("VACATE_TYPE vacateType");//请假类型 0:年假 1：事假 2：病假 3：调休 4：产假
            queryColumn.add("BEGIN_TIME beginTime");//开始时间
            queryColumn.add("END_TIME endTime");//结束时间
            queryColumn.add("DAYS days");//请假天数
            result = memberService.selectCustomColumnNamesList(HsUserVacateSheet.class,condition);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return result;
            }
            List<Map<Object,Object>> vacateList = (List<Map<Object, Object>>) result.getDataSet();
            Map<Object,Object> vacateTmp = null;
            for (String dayListOfMonth : dayListOfMonths) {
                int state = -1;//考勤状态 -1：缺勤 0:年假 1：事假 2：病假 3：调休 4：产假 5:休息 6： 正常打卡
                int day = StringUtil.getAsInt(dayListOfMonth.substring(8, dayListOfMonth.length()));
                vacateTmp = Maps.newHashMap();
                //判断是否有打卡
                for (int i = attendanceList.size()-1 ;i>=0 ; i--) {
                    Map<Object, Object> attendance = attendanceList.get(i);
                    String postTime = StringUtil.trim(attendance.get("postTime"));
                    String _postTime = postTime.substring(0,10);
                    if(dayListOfMonth.equals(_postTime)){
                        state = 6;//正常打卡
                        break;
                    }
                }
                if(-1==state){//如果没有打卡，则判断是否有请假
                    for (int i = vacateList.size()-1 ;i>=0 ; i--) {
                        Map<Object, Object> vacate = vacateList.get(i);
                        String beginTime = StringUtil.trim(vacate.get("beginTime"));
                        String endTime = StringUtil.trim(vacate.get("endTime"));
                        String _beginTime = beginTime.substring(0,10);
                        String _endTime = endTime.substring(0,10);
                        String substring = _beginTime.substring(8, _beginTime.length());
                        int beginDay =  StringUtil.getAsInt(substring);
                        int endDay =  StringUtil.getAsInt(_endTime.substring(8, _endTime.length()));
                        if(beginDay>endDay){
                            endDay = endDay+beginDay;
                        }
                        if(beginDay<=day && day< endDay){//判断是否有请假
                            state = StringUtil.getAsInt(StringUtil.trim(vacate.get("vacateType")));
                        }
                    }
                }
                vacateTmp.put(dayListOfMonth,state);
                data.add(vacateTmp);
            }
            result.setDataSet(data);

        } catch (Exception e) {
            logger.warn("Remote call to queryUserAttendances fails"+e);
            result.setDataSet(null);
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 打卡
     * @param condition
     * @return
     */
    @Override
    public ResultVo clockIn(Map<Object, Object> condition) {
        ResultVo result = null;
        try {
            HsUserAttendanceSheet hsUserAttendanceSheet = new HsUserAttendanceSheet();
            hsUserAttendanceSheet.setUserId(StringUtil.getAsInt(StringUtil.trim(condition.get("userId"))));
            hsUserAttendanceSheet.setPostTime(new Date());
            hsUserAttendanceSheet.setLatitude(StringUtil.trim(condition.get("latitude")));//纬度
            hsUserAttendanceSheet.setLongitude(StringUtil.trim(condition.get("longitude"))); //经度
            hsUserAttendanceSheet.setPostLocation(StringUtil.trim(condition.get("location"))); //打卡位置
            result = memberService.clockIn(hsUserAttendanceSheet);
        } catch (Exception e) {
            logger.warn("Remote call to clockIn fails"+e);
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 主管设置月度排班
     * @param condition
     * @return
     */
    @Override
    public ResultVo scheduleMonthlyAttendance(Map<Object, Object> condition) {
        ResultVo result = null;
        try {
            Map<Object, Object> datas = (Map<Object, Object>) condition.get("datas");
            //主管ID
            Integer _userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            result = memberService.clearAttendance(condition);
            List<HsUserVacateSheet> sheets = Lists.newArrayListWithCapacity(datas.size());
            HsUserVacateSheet sheet = null;
            long time = 24*60*60*1000;//1天
            Date nowTime = new Date();
            for (Map.Entry<Object, Object> entry : datas.entrySet()){
                String key = StringUtil.trim(entry.getKey());
                String _userIds = StringUtil.trim(entry.getValue());
                List<String> userIds = Arrays.asList(_userIds.split(","));
                Date beginTime = DateUtils.reverse2Date(key);//开始时间
                Date endTime = new Date(beginTime.getTime() + time);//结束时间
                for (String userId : userIds) {
                    sheet = new HsUserVacateSheet();
                    sheet.setBeginTime(beginTime);
                    sheet.setEndTime(endTime);
                    sheet.setDays(1);
                    sheet.setVacateReason("排班休息");
                    sheet.setCreateBy(_userId);
                    sheet.setCreateTime(nowTime);
                    sheet.setUserId(StringUtil.getAsInt(userId));
                    sheet.setVacateType(5);
                    sheet.setHandlerUserId(_userId);
                    sheet.setIsAgree(1);
                    sheet.setRemarks("排班休息");
                    sheets.add(sheet);
                }
            }
            if(sheets!=null && sheets.size()>0){
                condition.clear();
                condition.put("data",sheets);
                result = memberService.batchInsert(sheet,condition);
            }
        } catch (Exception e) {
            logger.warn("Remote call to scheduleMonthlyAttendance fails"+e);
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 主管查询个人月度考勤
     * @param condition
     * @return
     */
    @Override
    public ResultVo queryUserAttendance(Map<Object, Object> condition) {
        ResultVo result = null;
        try {
            int queryMonthsAgo = StringUtil.getAsInt(StringUtil.trim(condition.get("queryMonthsAgo")));

            //返回的结果集
            List<Map<Object,Object>> data = Lists.newArrayList();
            List<String> dayListOfMonths = DateUtil.getMonthFullDays(queryMonthsAgo);
            List<String> queryColumn = new ArrayList<>();
            queryColumn.add("ID attendanceId");///考勤表Id
            queryColumn.add("POST_TIME postTime");//打卡时间
            condition.put("queryColumn",queryColumn);
            //查询业务员打卡记录
            result = memberService.selectCustomColumnNamesList(HsUserAttendanceSheet.class,condition);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return result;
            }
            List<Map<Object,Object>> attendanceList = (List<Map<Object, Object>>) result.getDataSet();

            //查询业务员请假记录
            queryColumn.clear();
            queryColumn.add("ID vacateId");//请假记录Id
            queryColumn.add("VACATE_TYPE vacateType");//请假类型 0:年假 1：事假 2：病假 3：调休 4：产假
            queryColumn.add("BEGIN_TIME beginTime");//开始时间
            queryColumn.add("END_TIME endTime");//结束时间
            queryColumn.add("DAYS days");//请假天数
            result = memberService.selectCustomColumnNamesList(HsUserVacateSheet.class,condition);
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                return result;
            }
            List<Map<Object,Object>> vacateList = (List<Map<Object, Object>>) result.getDataSet();
            Map<Object,Object> vacateTmp = null;
            for (String dayListOfMonth : dayListOfMonths) {
                int state = -1;//考勤状态 -1：缺勤 0:年假 1：事假 2：病假 3：调休 4：产假 5:休息 6： 正常打卡
                int day = StringUtil.getAsInt(dayListOfMonth.substring(8, dayListOfMonth.length()));
                vacateTmp = Maps.newHashMap();
                //判断是否有打卡
                for (int i = attendanceList.size()-1 ;i>=0 ; i--) {
                    Map<Object, Object> attendance = attendanceList.get(i);
                    String postTime = StringUtil.trim(attendance.get("postTime"));
                    String _postTime = postTime.substring(0,10);
                    if(dayListOfMonth.equals(_postTime)){
                        state = 6;//正常打卡
                        break;
                    }
                }
                if(-1==state){//如果没有打卡，则判断是否有请假
                    for (int i = vacateList.size()-1 ;i>=0 ; i--) {
                        Map<Object, Object> vacate = vacateList.get(i);
                        String beginTime = StringUtil.trim(vacate.get("beginTime"));
                        String endTime = StringUtil.trim(vacate.get("endTime"));
                        String _beginTime = beginTime.substring(0,10);
                        String _endTime = endTime.substring(0,10);
                        String substring = _beginTime.substring(8, _beginTime.length());
                        int beginDay =  StringUtil.getAsInt(substring);
                        int endDay =  StringUtil.getAsInt(_endTime.substring(8, _endTime.length()));
                        if(beginDay>endDay){
                            endDay = endDay+beginDay;
                        }
                        if(beginDay<=day && day< endDay){//判断是否有请假
                            state = StringUtil.getAsInt(StringUtil.trim(vacate.get("vacateType")));
                        }
                    }
                }
                vacateTmp.put(dayListOfMonth,state);
                data.add(vacateTmp);
            }
//            resultMap.put("vacates",vacateList);//请假记录
//            resultMap.put("attendance",attendanceList);//考勤记录
            result.setDataSet(data);

            return result;
        } catch (Exception e) {
            logger.warn("Remote call to scheduleMonthlyAttendance fails"+e);
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 主管修改个人月度考勤
     * @param condition
     * @return
     */
    @Override
    public ResultVo settingAttendance(Map<Object, Object> condition) {
        ResultVo result = new ResultVo();
        try {
            Map<Object, Object> datas = (Map<Object, Object>) condition.get("datas");
            if(datas == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            }
            int settingUserId = (int) StringUtil.getDouble(StringUtil.trim(datas.get("userId")));
            int queryMonthsAgo = (int) StringUtil.getDouble(StringUtil.trim(datas.get("queryMonthsAgo")),0);
            //操作人ID
            Integer userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            //正常打卡
            List<String> clocks = (List<String>) datas.get("clocks");
            //休息日
            List<Map<Object, Object>> holidays = (List<Map<Object, Object>>) datas.get("holidays");
            //请假记录
            List<HsUserVacateSheet> vacateSheets = Lists.newArrayList();
            HsUserVacateSheet vacateSheet = null;
            //打卡记录
            List<HsUserAttendanceSheet> attendanceSheets = Lists.newArrayList();
            HsUserAttendanceSheet attendanceSheet = null;
            long time = 24*60*60*1000;//1天
            for (String clock : clocks) {
                Date beginTime = DateUtils.reverse2Date(clock);
                attendanceSheet = new HsUserAttendanceSheet();
                attendanceSheet.setUserId(settingUserId);
                attendanceSheet.setPostTime(beginTime);
                attendanceSheet.setRemarks("主管修改正常打卡");
                attendanceSheets.add(attendanceSheet);
            }
            for (Map<Object, Object> holiday : holidays) {
                for (Map.Entry<Object, Object> entry : holiday.entrySet()) {
                    int state = (int) StringUtil.getDouble(StringUtil.trim(entry.getValue()));
                    if(state == -1 || state == 6){
                        continue;
                    }
                    Date beginTime = DateUtils.reverse2Date((String) entry.getKey());
                    Date endTime = new Date(beginTime.getTime() + time);//结束时间
                    vacateSheet = new HsUserVacateSheet();
                    vacateSheet.setBeginTime(beginTime);
                    vacateSheet.setEndTime(endTime);
                    vacateSheet.setDays(1);
                    String remarks = "";
                    if(state == 0){
                        remarks = "主管设置年假";
                    }else if(state == 1){
                        remarks = "主管设置事假";
                    }else if(state == 2){
                        remarks = "主管设置病假";
                    }else if(state == 3){
                        remarks = "主管设置调休";
                    }else if(state == 4){
                        remarks = "主管设置产假";
                    }else if(state == 5){
                        remarks = "主管设置休息";
                    }
                    vacateSheet.setVacateReason(remarks);
                    vacateSheet.setCreateBy(userId);
                    vacateSheet.setCreateTime(beginTime);
                    vacateSheet.setUserId(settingUserId);
                    vacateSheet.setVacateType(state);
                    vacateSheet.setHandlerUserId(userId);
                    vacateSheet.setIsAgree(1);
                    vacateSheet.setRemarks(remarks);
                    vacateSheets.add(vacateSheet);
                }
            }
            condition.clear();
            condition.put("queryMonthsAgo",queryMonthsAgo);
            condition.put("userId",settingUserId);
            result = memberService.clearUserAttendance(condition);
            if(vacateSheets!=null && vacateSheets.size()>0){//请假记录
                condition.clear();
                condition.put("data",vacateSheets);
                result = memberService.batchInsert(vacateSheet,condition);
            }
            if(attendanceSheets!=null && attendanceSheets.size()>0){//打卡记录
                condition.clear();
                condition.put("data",attendanceSheets);
                result = memberService.batchInsert(attendanceSheet,condition);
            }


        } catch (Exception e) {
            logger.warn("Remote call to settingAttendance fails"+e);
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_FAILURE,ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return result;
    }

    /**
     * 是否新用户
     * @param condition
     * @return
     */
    @Override
    public ResultVo isNewUser(Map<Object, Object> condition){
        ResultVo resultVo = new ResultVo();
        try{
            String validateCode = StringUtil.trim(condition.get("validateCode"));
            String ip = StringUtil.trim(condition.get("ip"));
            //区号
            String areaCode = StringUtil.trim(condition.get("areaCode"));
            //手机号
            String mobile = StringUtil.trim(condition.get("memberMoble"));
            String cacheKey = RedisConstant.SYS_USER_REGISTER_IMG_CODE_KEY + areaCode + mobile + ip;
            String cacheValidateCode = "";
            if(!StringUtil.hasText(cacheKey)){
                resultVo.setResult(ResultConstant.SYS_IMG_CODE_IS_OVERDUE);
                resultVo.setMessage(ResultConstant.SYS_IMG_CODE_IS_OVERDUE_VALUE);
                return resultVo;
            }

            if(RedisUtil.isExistCache(cacheKey)){
                cacheValidateCode = RedisUtil.safeGet(cacheKey);
            }
            if(!StringUtil.hasText(StringUtil.trim(condition.get("isTest")))){
                if(!cacheValidateCode.equalsIgnoreCase(validateCode)){
                    //验证码错误
                    resultVo.setResult(ResultConstant.SYS_IMG_CODE_ERROR);
                    resultVo.setMessage(ResultConstant.SYS_IMG_CODE_ERROR_VALUE);
                    return resultVo;
                }

            }

            List<String> queryColumn = new ArrayList<>();
            condition.clear();
            queryColumn.add("ID id");
            condition.put("queryColumn",queryColumn);
            condition.put("memberMoble", mobile);
            resultVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                List<Map<Object, Object>> hsMemberList = (List<Map<Object, Object>>) resultVo.getDataSet();
                if(hsMemberList == null || hsMemberList.size() < 1){
                    //当前用户为新用户
                    resultVo.setResult(ResultConstant.USER_NOT_REGISTERED);
                    resultVo.setMessage(ResultConstant.USER_NOT_REGISTERED_VALUE);
                    return resultVo;
                }
                resultVo.setDataSet(hsMemberList.get(0));
            }
        }catch (Exception e){
            logger.warn("Remote call to isNewUser fails"+e);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            return resultVo;
        }
        return resultVo;
    }

    /**
     * 加载下级员工
     * @param condition
     * @return
     */
    @Override
    public ResultVo getSubordinatePosition(Map<Object, Object> condition) {
        ResultVo resultVo = null;
        try{
            resultVo = memberService.getSubordinatePosition(condition);
        }catch (Exception e){
            logger.error("MemberServiceImpl getSubordinatePosition Exception message:" + e);
            resultVo = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取贷款信息列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo getLoansList(Map<Object, Object> condition){
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = memberService.selectList(new HsMemberFinancialLoansApply(), condition, 0);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取贷款详情
     * @param id
     * @return
     */
    @Override
    public ResultVo getLoansdetail(Integer id){
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = memberService.select(id,new HsMemberFinancialLoansApply());
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 更新人员贷款信息
     * @param memberFinancialLoansApply
     * @return
     */
    @Override
    public ResultVo updateLoans(HsMemberFinancialLoansApply memberFinancialLoansApply){
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = memberService.update(memberFinancialLoansApply);
        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 获取预约看房列表
     * @param condition
     * @return
     */
    @Override
    public ResultVo appointmentLookHouseList(Map<Object, Object> condition){
        ResultVo result;
        try {
            result = memberService.selectList(new HsMemberHousingApplication(),condition,0);
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && result.getDataSet() != null){
                List<Map<Object, Object>> housingApplicationList = (List<Map<Object, Object>>) result.getDataSet();
                List<Integer> houseIds = new ArrayList<>();
                List<Integer> memberIds = new ArrayList<>();
                for (Map<Object, Object> housingApplicationMap : housingApplicationList) {
                    //房源id
                    int houseId = StringUtil.getAsInt(StringUtil.trim(housingApplicationMap.get("houseId")));
                    //业主id
                    int ownerId = StringUtil.getAsInt(StringUtil.trim(housingApplicationMap.get("ownerId")));
                    //客户id
                    int memberId = StringUtil.getAsInt(StringUtil.trim(housingApplicationMap.get("memberId")));
                    if(houseId > 0){
                        houseIds.add(houseId);
                    }
                    if(ownerId > 0){
                        memberIds.add(ownerId);
                    }
                    if(memberId > 0){
                        memberIds.add(memberId);
                    }
                }
                /**
                 * 获取房源信息
                 */
                List<Map<Object,Object>> houseList = new ArrayList<>();
                condition.clear();
                condition.put("houseIds",houseIds);
                ResultVo houseResultVo = housesService.selectList(new HsMainHouse(), condition, 0);
                if(houseResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && houseResultVo.getDataSet() != null){
                    houseList = (List<Map<Object,Object>>)houseResultVo.getDataSet();
                }
                /**
                 * 获取人员信息（业主，买家）
                 */
                List<Map<Object, Object>> memberList = new ArrayList<>();
                //自定义查询列名
                List<String> queryColumn = new ArrayList<>();
                condition.clear();
                queryColumn.add("ID memberId");
                //姓氏
                queryColumn.add("FAMILY_NAME familyName");
                //名称
                queryColumn.add("NAME name");
                //电话地区号
                queryColumn.add("AREA_CODE areaCode");
                //手机号
                queryColumn.add("MEMBER_MOBLE memberMoble");
                condition.put("queryColumn", queryColumn);
                condition.put("memberIds", memberIds);
                condition.put("isDel", 0);
                ResultVo memberResultVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                if(memberResultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && memberResultVo.getDataSet() != null){
                    memberList = (List<Map<Object, Object>>) memberResultVo.getDataSet();
                }
                /**
                 * 封装信息
                 */
                List<Map<Object, Object>> resultList = new ArrayList<>();
                for (Map<Object, Object> housingApplicationMap : housingApplicationList) {
                    //租客姓名
                    String rentCustomerName = "";
                    //租客手机号
                    String rentCustomerPhone = "";
                    //城市
                    String city = "";
                    //社区
                    String community = "";
                    //子社区
                    String subCommunity = "";
                    //房源所在区域名称
                    String address = "";
                    //房源主图
                    String houseMainImg = "";
                    //房源id
                    int houseId = StringUtil.getAsInt(StringUtil.trim(housingApplicationMap.get("houseId")));
                    //业主id
                    int ownerId = StringUtil.getAsInt(StringUtil.trim(housingApplicationMap.get("ownerId")));
                    //客户id
                    int memberId = StringUtil.getAsInt(StringUtil.trim(housingApplicationMap.get("memberId")));
                    //封装房源信息
                    Optional<Map<Object, Object>> houseOptional = houseList.stream().filter(map -> StringUtil.getAsInt(StringUtil.trim(map.get("id"))) == houseId).findFirst();
                    if(houseOptional.isPresent()){
                        Map<Object, Object> houseMap = houseOptional.get();
                        rentCustomerName = StringUtil.trim(houseMap.get("rentCustomerName"));
                        rentCustomerPhone = StringUtil.trim(houseMap.get("rentCustomerPhone"));
                        city = StringUtil.trim(houseMap.get("city"));
                        community = StringUtil.trim(houseMap.get("community"));
                        subCommunity = StringUtil.trim(houseMap.get("subCommunity"));
                        address = StringUtil.trim(houseMap.get("address"));
                        houseMainImg = ImageUtil.imgResultUrl(StringUtil.trim(houseMap.get("houseMainImg")));

                    }
                    housingApplicationMap.put("rentCustomerName",rentCustomerName);
                    housingApplicationMap.put("rentCustomerPhone",rentCustomerPhone);
                    housingApplicationMap.put("city",city);
                    housingApplicationMap.put("community",community);
                    housingApplicationMap.put("subCommunity",subCommunity);
                    housingApplicationMap.put("address",address);
                    housingApplicationMap.put("houseMainImg",houseMainImg);
                    //封装业主、客户信息
                    for (Map<Object, Object> memberMap : memberList) {
                        int memberID = StringUtil.getAsInt(StringUtil.trim(memberMap.get("memberId")));
                        if(memberID == memberId){
                            housingApplicationMap.put("member",memberMap);
                        }else if(memberID == ownerId){
                            housingApplicationMap.put("owner",memberMap);
                        }
                    }
                    resultList.add(housingApplicationMap);
                }
                result.setDataSet(resultList);
            }
        }catch (Exception e){
            e.printStackTrace();
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }

    /**
     * 获取预约看房详情预约看房id
     * @param id 预约看房id
     * @return
     */
    @Override
    public ResultVo appointmentLookHouseDetail(Integer id){
        ResultVo resultVo = new ResultVo();
        try {
            resultVo = memberService.select(id,new HsMemberHousingApplication());
            if(resultVo.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS && resultVo.getDataSet() != null){
                HsMemberHousingApplication housingApplication = (HsMemberHousingApplication) resultVo.getDataSet();
                //预约类型（0：申请预约，1：无需预约，参与看房，2：让客服联系 3:联系租客）
                Integer applicationType = housingApplication.getApplicationType();
                //房源id
                Integer houseId = housingApplication.getHouseId();
                //客户id
                Integer memberId = housingApplication.getMemberId();
                //业主id
                Integer ownerId = housingApplication.getOwnerId();
                List<Integer> memberIds = new ArrayList<>();
                memberIds.add(memberId);
                memberIds.add(ownerId);

                Map<Object,Object> condition = new HashMap<>(16);
                /**
                 * 获取房源信息
                 */
                //租客姓名
                String rentCustomerName = "";
                //租客手机号
                String rentCustomerPhone = "";
                //城市
                String city = "";
                //社区
                String community = "";
                //子社区
                String subCommunity = "";
                //房源所在区域名称
                String address = "";
                //房源名称
                String houseName = "";
                //房源编号
                String houseCode = "";
                ResultVo houseResultVo = housesService.select(houseId, new HsMainHouse());
                if(houseResultVo.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS){
                    return houseResultVo;
                }
                HsMainHouse house = (HsMainHouse) houseResultVo.getDataSet();
                if(house != null){
                    rentCustomerName = house.getRentCustomerName();
                    rentCustomerPhone = house.getRentCustomerPhone();
                    city = house.getCity();
                    community = house.getCommunity();
                    subCommunity = house.getSubCommunity();
                    address = house.getAddress();
                    houseName = house.getHouseName();
                    houseCode = house.getHouseCode();
                }


                /**
                 * 获取人员信息（业主，买家）
                 */
                //自定义查询列名
                List<String> queryColumn = new ArrayList<>();
                queryColumn.clear();
                condition.clear();
                queryColumn.add("ID memberId");
                //姓氏
                queryColumn.add("FAMILY_NAME familyName");
                //名称
                queryColumn.add("NAME name");
                //电话地区号
                queryColumn.add("AREA_CODE areaCode");
                //手机号
                queryColumn.add("MEMBER_MOBLE memberMoble");
                condition.put("queryColumn", queryColumn);
                condition.put("memberIds", memberIds);
                condition.put("isDel", 0);
                ResultVo memberVo = memberService.selectCustomColumnNamesList(HsMember.class, condition);
                if(memberVo.getResult()!= ResultConstant.SYS_REQUIRED_SUCCESS){
                    return memberVo;
                }
                List<Map<Object, Object>> memberList = (List<Map<Object, Object>>) memberVo.getDataSet();

                /**
                 * 封装信息
                 */
                Map<String,Object> resultMap = new HashMap<>(16);
                JSONObject jsonObject = JSON.parseObject(JSONObject.toJSONString(housingApplication, SerializerFeature.WriteMapNullValue));
                resultMap = JSON.toJavaObject(jsonObject, Map.class);
                //封装人员信息
                if(memberList != null){
                    for (Map<Object, Object> memberMap : memberList) {
                        int memberID = StringUtil.getAsInt(StringUtil.trim(memberMap.get("memberId")));
                        if(memberID == memberId){
                            resultMap.put("member",memberMap);
                        }else if(memberID == ownerId){
                            resultMap.put("owner",memberMap);
                        }
                    }
                }
                //封装房源信息
                resultMap.put("rentCustomerName",rentCustomerName);
                resultMap.put("rentCustomerPhone",rentCustomerPhone);
                resultMap.put("address",address);
                resultMap.put("subCommunity",subCommunity);
                resultMap.put("community",community);
                resultMap.put("city",city);
                resultMap.put("houseCode",houseCode);
                resultMap.put("houseName",houseName);
                resultVo.setDataSet(resultMap);
            }

        }catch (Exception e){
            e.printStackTrace();
            resultVo.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return resultVo;
    }

    /**
     * 更新预约看房信息
     * @param condition
     * @return
     */
    @Override
    public ResultVo appointmentLookHouseUpdate(Map<Object, Object> condition){
        ResultVo result = new ResultVo();
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            int id = StringUtil.getAsInt(StringUtil.trim(condition.get("id")));
            String startApartmentTime = StringUtil.trim(condition.get("startApartmentTime"));
            int userId = StringUtil.getAsInt(StringUtil.trim(condition.get("userId")));
            String remark = StringUtil.trim(condition.get("remark"));
            //结束看房时间点
            Date beginTime = sdf.parse(startApartmentTime);
            String endStrTime = DateUtil.addDateMinut(startApartmentTime,1);
            //结束看房时间点
            Date endTime = sdf.parse(endStrTime);
            if(beginTime.before(date)){
                result.setResult(ResultConstant.BUS_MEMBER_APPOINTMENT_FAILURE);
                result.setMessage(ResultConstant.BUS_MEMBER_APPOINTMENT_FALIURE_VALUE+":The time period you have reserved has expired。");
                return result;
            }

            //获取预约看房信息
            result = memberService.select(id,new HsMemberHousingApplication());
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || result.getDataSet() == null){
                return ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,"Abnormal reservation information");
            }
            //预约看房信息
            HsMemberHousingApplication housingApplication = (HsMemberHousingApplication) result.getDataSet();
            Integer houseId = housingApplication.getHouseId();
            //获取房源信息
            result = housesService.select(houseId,new HsMainHouse());
            if(result.getResult() != ResultConstant.SYS_REQUIRED_SUCCESS || result.getDataSet() == null){
                //房源不存在
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("该房源不存在");
                return result;
            }
            //房源信息
            HsMainHouse mainHouse = (HsMainHouse) result.getDataSet();
            if(mainHouse.getIsDel() != 0 || mainHouse.getIsLock() != 0){
                //房源被删除或者已经锁定
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("房源被删除或者已经锁定");
                return result;
            }
            housingApplication.setStartApartmentTime(sdf.parse(startApartmentTime));
            //是否处理：0>客服无需处理，1>客服待处理，2>客服已处理
            housingApplication.setIsDispose(2);
            housingApplication.setEndApartmentTime(endTime);
            housingApplication.setUpdateBy(userId);
            housingApplication.setUpdateTime(date);
            if(StringUtil.hasText(remark)){
                housingApplication.setRemark(remark);
            }
            if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                SimpleDateFormat osdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //字符串类型抢单结束时间
                String closeTimeStr = DateUtil.getTimeByMinute(osdf.format(new Date()),30,1);
                //生成外看订单
                HsSystemOrderPool systemOrderPool = new HsSystemOrderPool();
                systemOrderPool.setHouseId(houseId);
                systemOrderPool.setOrderType(1);
                systemOrderPool.setOrderCode("SCO_"+RandomUtils.getRandomCode());
                //房源申请ID
                systemOrderPool.setApplyId(mainHouse.getApplyId());
                systemOrderPool.setCreateTime(date);
                systemOrderPool.setUpdateTime(date);
                systemOrderPool.setOpenOrderCloseTime(osdf.parse(closeTimeStr));
                //开启抢单
                systemOrderPool.setIsOpenOrder(0);
                systemOrderPool.setCreateBy(userId);
                systemOrderPool.setUpdateBy(userId);
                //见面地点
                systemOrderPool.setAppointmentMeetPlace(mainHouse.getCity()+mainHouse.getCommunity()+mainHouse.getSubCommunity()+mainHouse.getAddress());
                //给开始看房时间，结束看房时间赋值
                systemOrderPool.setEstimatedTime(sdf.parse(startApartmentTime));
                //分配订单时，将系统分配的时间为准，必须在48小时内完成
                systemOrderPool.setCloseTime(new Date(date.getTime()+48*60*60*1000));
                //添加备注信息
                systemOrderPool.setRemark("客服确认预约看房加入订单池");
                //新增系统订单池数据
                result = orderService.insert(systemOrderPool);
                if(result.getResult() == ResultConstant.SYS_REQUIRED_SUCCESS){
                    //在系统订单池表中插入数据
                    HsSystemOrderPool pool = (HsSystemOrderPool) result.getDataSet();
                    HsSystemOrderPoolLog systemOrderPoolLog = new HsSystemOrderPoolLog();
                    //订单池主键ID
                    systemOrderPoolLog.setPoolId(pool.getId());
                    //外看订单
                    systemOrderPoolLog.setOrderType(1);
                    //加入订单池
                    systemOrderPoolLog.setNodeType(0);
                    systemOrderPoolLog.setCreateBy(pool.getCreateBy());
                    systemOrderPoolLog.setCreateTime(date);
                    systemOrderPoolLog.setPostTime(date);
                    //操作人用户ID
                    systemOrderPoolLog.setOperatorUid(pool.getCreateBy());
                    //操作人类型1:普通会员 2:商家 3:系统管理员
                    systemOrderPoolLog.setOperatorType(3);
                    systemOrderPoolLog.setRemarks("客服确认预约看房加入订单池");
                    //设置订单池ID
                    housingApplication.setStandby1(pool.getId().toString());
                    //新增系统订单池日志信息
                    result = orderService.insert(systemOrderPoolLog);
                }
            }
            result = memberService.update(housingApplication);
        }catch (Exception e){
            e.printStackTrace();
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return result;
    }
}
