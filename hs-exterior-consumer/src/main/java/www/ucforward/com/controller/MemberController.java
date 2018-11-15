package www.ucforward.com.controller;


import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.utils.StringUtil;
import www.ucforward.com.annotation.NoRequireLogin;
import www.ucforward.com.constants.AppRquestParamsConstant;
import www.ucforward.com.constants.ResultConstant;
import www.ucforward.com.dto.HsMemberDto;
import www.ucforward.com.entity.*;
import www.ucforward.com.manager.CommonManager;
import www.ucforward.com.manager.HousesManager;
import www.ucforward.com.manager.MemberManager;
import www.ucforward.com.manager.PayManager;
import www.ucforward.com.umeng.PushClient;
import www.ucforward.com.utils.*;
import www.ucforward.com.vo.PayLoadVo;
import www.ucforward.com.vo.ResultVo;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Null;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author wenbn
 * @version 1.0
 * @date 2018/5/22
 */
@Controller
@RequestMapping("member")
@ResponseBody
public class MemberController {

    private static Logger logger = LoggerFactory.getLogger(MemberController.class); // 日志记录

    public static final String ANDROID_APPKEY = PropertiesUtils.loadProps("/umeng/umeng.properties").getProperty("test_android_appkey");
    public static final String ANDROID_MESSAGE_SECRET = PropertiesUtils.loadProps("/umeng/umeng.properties").getProperty("test_android_messageSecret");

    @Resource
    private MemberManager memberManager;
    @Resource
    private HousesManager housesManager;
    @Resource
    private PayManager payManager;
    @Resource
    private CommonManager commonManager;


    /**
     * 获取地址编码
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/load/city/code")
    @NoRequireLogin
    public String loadCityCode(HttpServletRequest request) throws Exception {
        Map<Object, Object> condition = new HashMap<Object, Object>();
//        int pageIndex = StringUtil.getAsInt(StringUtil.trim(request.getParameter("pageIndex"),"0"));
//        condition.put("pageIndex",pageIndex);
//        condition.put("pageSize", 50);
        condition.put("isDel", 0);
        ResultVo vo = memberManager.loadCityCode(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 登录接口，NoRequireLogin不需要登录就能访问
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/login")
    @NoRequireLogin
    public String login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultVo vo = new ResultVo();
        Map<Object, Object> map = RequestUtil.getParameterMap(request);
        int language = StringUtil.getAsInt(StringUtil.trim(map.get("language")), 0);//语言版本
        String memberMoble = StringUtil.trim(map.get("moble"));//手机号码
        String areaCode = StringUtil.trim(map.get("areaCode"), "971");//电话地区号
        String validateCode = StringUtil.trim(map.get("validateCode"));//验证码
        String invitationCode = StringUtil.trim(map.get("invitationCode"));//邀请码

        String familyName = StringUtil.trim(request.getParameter("familyName"));//验证码
        String name = StringUtil.trim(request.getParameter("name"));//邀请码

        String isTest = StringUtil.trim(map.get("isTest"));//是否测试登录
        Map<Object, Object> condition = new HashMap<Object, Object>();
        if (!StringUtil.hasText(memberMoble) || !StringUtil.hasText(validateCode)) {
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(vo);
        }
        condition.put("memberMoble", memberMoble);
        condition.put("areaCode", areaCode);
        condition.put("validateCode", validateCode);
        condition.put("isTest", isTest);
        condition.put("familyName", familyName);
        condition.put("name", name);
        condition.put("ip", RequestUtil.getIpAddress(request));
        condition.put("invitationCode", invitationCode);//邀请码
        condition.put("language", language);
        vo = memberManager.login(condition);
        return JsonUtil.toJson(vo);
    }

    /**
     * 登录接口，NoRequireLogin不需要登录就能访问
     *
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResultVo vo = new ResultVo();
        Map<Object, Object> map = RequestUtil.getParameterMap(request);
        String token = request.getParameter("token");
        String[] params = token.split("\\.");
        if (params.length != 3) {
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(vo);
        }

        PayLoadVo payLoadVo = JsonUtil.jsonToObjectT(EncryptionUtil.getFromBase64(params[1]), PayLoadVo.class);
        String signature = EncryptionUtil.getFromBase64(params[2]);
        if (payLoadVo == null || StringUtils.isBlank(signature)) {
            vo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            vo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(vo);
        }
        if(RedisUtil.isExistCache(payLoadVo.getIss())){
            RedisUtil.safeDel(payLoadVo.getIss());
        }
        return JsonUtil.toJson(vo);
    }



    /**
     * 是否新用户
     * @param request
     * @return
     */
    @RequestMapping("/isNewUser")
    @NoRequireLogin
    public String isNewUser(HttpServletRequest request){
        ResultVo resultVo = new ResultVo();
        //ip地址
        String ip = RequestUtil.getIpAddress(request);
        //手机号码
        String mobile = request.getParameter("mobile");
        //电话地区号
        String areaCode = StringUtil.trim(request.getParameter("areaCode"), "971");
        //验证码
        String validateCode = request.getParameter("validateCode");
        //是否测试登录
        String isTest = StringUtil.trim(request.getParameter("isTest"));
        if(!StringUtil.hasText(mobile) || !StringUtil.hasText(validateCode)){
            resultVo.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
            resultVo.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(resultVo);
        }

        Map<Object, Object> condition = new HashMap<>(5);
        condition.put("memberMoble", mobile);
        condition.put("areaCode", areaCode);
        condition.put("validateCode", validateCode);
        condition.put("isTest", isTest);
        condition.put("ip", RequestUtil.getIpAddress(request));
        ResultVo newUser = memberManager.isNewUser(condition);
        return JsonUtil.toJson(newUser);
    }

    @RequestMapping("/register")
    public String register(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //过滤特殊字符
        Map<Object, Object> map = RequestUtil.getParameterMap(request);
        String language = StringUtil.trim(map.get("language"), "0");
        return null;
    }

    /**
     * 发送短信验证码
     *
     * @param request
     * @param areaCode
     * @param mobile
     * @return
     */
    @RequestMapping(value = "sendSmsValidateCode", method = RequestMethod.POST)
    @NoRequireLogin
    public String sendSmsValidateCode(HttpServletRequest request, String areaCode, String mobile) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            if (!StringUtil.hasText(areaCode) || !StringUtil.hasText(mobile)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.put("areaCode", areaCode);
            condition.put("mobile", mobile);
            condition.put("ip", RequestUtil.getIpAddress(request));
            result = memberManager.sendSmsValidateCode(condition);
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 获取用户信息接口
     *
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping("/info/get")
    public String getMemberInfo(@RequestParam(required = false) String token) throws Exception {
        ResultVo result = null;
        String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
        if (!StringUtil.hasText(memberId)) {
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(result);
        }
        result = memberManager.getMemberInfo(StringUtil.getAsInt(memberId));
        return JsonUtil.toJson(result);
    }

    /**
     * 获取用户信息接口
     *
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping("/info/update")
    public String updateMemberInfo(HttpServletRequest request, @RequestParam(required = false) String token, HsMemberDto memberDto, String validateCode) throws Exception {
        ResultVo result = null;
        int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId(), 0); //会员ID
        if (memberId == 0) {
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(result);
        }
        HsMember member = new HsMember();
        ModelMapperUtil.getInstance().map(memberDto, member);
        member.setId(memberId);
        member.setBlog(null);//积分帐户,默认为0,支持小数位
        member.setCapital(null);//资金账户(可提现可充值账户)
        member.setCcapital(null);//资金账户(不可提现，不可充值，适用礼包赠送资金等)
        if (StringUtil.hasText(member.getMemberMoble())) {//若修改手机号
            if (!StringUtil.hasText(validateCode)) {//若短信验证码为空时，返回请求参数错误
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, "请输入短信验证码"));
            }
            Map<Object, Object> condition = Maps.newHashMap();
            condition.put("data", member);
            condition.put("validateCode", validateCode);
            condition.put("ip", RequestUtil.getIpAddress(request));
            condition.put("isTest", 0);//测试使用
            result = memberManager.updateMemberInfo(condition);
        } else {
            result = memberManager.updateMemberInfo(member);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取用户信息接口
     *
     * @param token
     * @return
     * @throws Exception
     */
    @RequestMapping("/info/update/nickName")
    public String updateMemberInfo(@RequestParam(required = false) String token, @RequestParam(required = false) String nickName) throws Exception {
        ResultVo result = null;
        int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId(), 0); //会员ID
        if (memberId == 0) {
            result = ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            return JsonUtil.toJson(result);
        }
        HsMember member = new HsMember();
        member.setId(memberId);
        member.setNickname(nickName);
        result = memberManager.updateMemberInfo(member);
        return JsonUtil.toJson(result);
    }

    /**
     * 新增会员浏览历史记录
     *
     * @param request
     * @param response
     * @param browseHistory
     * @return
     */
    @RequestMapping(value = "addBrowseHistory", method = RequestMethod.POST)
    public String addBrowseHistory(HttpServletRequest request, HttpServletResponse response, HsHousesBrowseHistory browseHistory) {
        ResultVo result = new ResultVo();
        try {
            Object requestObj = RequestUtil.handleRequestBeanData(browseHistory);
            browseHistory = (HsHousesBrowseHistory) requestObj;
            String houseId = browseHistory.getHouseId() == null ? "" : browseHistory.getHouseId().toString(); //房源ID
            String token = StringUtil.trim(request.getParameter("token"));
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (!StringUtil.hasText(houseId) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            browseHistory.setMemberId(Integer.parseInt(memberId));
            result = memberManager.addBrowseHistory(browseHistory);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController controller:addBrowseHistory Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage("MemberController controller:addBrowseHistory Exception message:" + e.getMessage());
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增会员收藏记录
     *
     * @param request
     * @param response
     * @param memberFavorite
     * @return
     */
    @RequestMapping(value = "addMemberFavorite", method = RequestMethod.POST)
    public String addMemberFavorite(HttpServletRequest request, HttpServletResponse response, HsMemberFavorite memberFavorite) {
        ResultVo result = new ResultVo();
        try {
            Object requestObj = RequestUtil.handleRequestBeanData(memberFavorite);
            memberFavorite = (HsMemberFavorite) requestObj;
            String favoriteId = memberFavorite.getFavoriteId() == null ? "" : memberFavorite.getFavoriteId().toString(); //房源ID
            String isFavorite = memberFavorite.getIsFavorite() == null ? "" : memberFavorite.getIsFavorite().toString();
            String token = StringUtil.trim(request.getParameter("token"));
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            // String memberId = memberFavorite.getMemberId().toString();
            if (!StringUtil.hasText(favoriteId) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            memberFavorite.setMemberId(Integer.parseInt(memberId));
            result = memberManager.addMemberFavorite(memberFavorite);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController controller:addMemberFavorite Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage("MemberController controller:addMemberFavorite Exception message:" + e.getMessage());
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取会员浏览记录信息列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMemberBrowseHistory", method = RequestMethod.POST)
    public String getMemberBrowseHistory(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<Object, Object>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String houseType = StringUtil.trim(map.get("houseType")); //房屋类型（0：出租，1：出售）
            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (!StringUtil.hasText(houseType) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1");
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));
            condition.put("houseType", houseType);
            condition.put("memberId", memberId);
            condition.put("languageVersion", languageVersion);
            condition.put("pageIndex", pageIndex);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
            result = memberManager.getMemberRelationInfo(new HsHousesBrowseHistory(), condition);
        } catch (Exception e) {
            logger.error("MemberController getMemberBrowseHistory Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 获取会员收藏房源信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMemberFavorite", method = RequestMethod.POST)
    public String getMemberFavorite(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();

        try {
            Map map = RequestUtil.getParameterMap(request);
            String favoriteType = StringUtil.trim(map.get("favoriteType"));
            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (!StringUtil.hasText(favoriteType) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1");
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));
            condition.put("favoriteType", favoriteType);
            condition.put("memberId", memberId);
            condition.put("languageVersion", languageVersion);
            condition.put("pageIndex", pageIndex);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
            condition.put("isDel", 0); //查询为已收藏的数据
            result = memberManager.getMemberRelationInfo(new HsMemberFavorite(), condition);
        } catch (Exception e) {
            logger.error("MemberController getMemberFavorite Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 获取会员房源列表信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMemberHousing", method = RequestMethod.POST)
    public String getMemberHousing(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<Object, Object>();

        try {
            Map map = RequestUtil.getParameterMap(request);
            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (!StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1");
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));
            condition.put("memberId", memberId);

            List<Integer> isCheckStatus = new ArrayList<>();
            isCheckStatus.add(1);
            isCheckStatus.add(2);
            condition.put("isCheckStatus", isCheckStatus);//已上架,业主申请下架的房源
           // condition.put("houseStatus", HOUSE_STATUS);//已上架,业主申请下架的房源
            condition.put("isDel", 0);//是否删除
            condition.put("languageVersion", languageVersion);
            condition.put("pageIndex", pageIndex); //当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //每页显示条数
            result = memberManager.getMemberHousing(condition);

        } catch (Exception e) {
            logger.error("MemberController getMemberHousing Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 会员申请下架
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "memberApplyWithdraw", method = RequestMethod.POST)
    // @NoRequireLogin
    public String memberApplyWithdraw(HttpServletRequest request, HttpServletResponse response, HsHouseObtained houseObtained) {
        ResultVo result = new ResultVo();
        try {
            String houseId = StringUtil.trim(houseObtained.getHouseId());
            String token = StringUtil.trim(request.getParameter("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            //String memberId = "12";
            if (!StringUtil.hasText(houseId) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            houseObtained.setCreateBy(StringUtil.getAsInt(memberId));
            result = memberManager.memberApplyWithdraw(houseObtained);
        } catch (Exception e) {
            logger.error("MemberController memberApplyWithdraw Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 修改房源租金信息
     *
     * @param request
     * @param response
     * @param hsMainHouse
     * @return
     */
    @RequestMapping(value = "updateHousingRent", method = RequestMethod.POST)
    //  @NoRequireLogin
    public String updateHousingRent(HttpServletRequest request, HttpServletResponse response, HsMainHouse hsMainHouse) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String id = StringUtil.trim(map.get("id")); //房源ID
            String houseRent = StringUtil.trim(map.get("houseRent")); //租金/售价
            String token = StringUtil.trim(request.getParameter("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            //String memberId = "12";
            if (!StringUtil.hasText(id) || !StringUtil.hasText(houseRent)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);
            condition.put("memberId", memberId);
            result = memberManager.updateHousingInfo(condition);
        } catch (Exception e) {
            logger.error("MemberController updateHousingRent Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 业主修改预约看房时间
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "updateHousingLookTime", method = RequestMethod.POST)
    // @NoRequireLogin
    public String updateHousingLookTime(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String id = StringUtil.trim(map.get("id")); //房源ID
            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            // String memberId = "12";

            if (!StringUtil.hasText(id)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);
            condition.put("memberId", memberId);
            result = memberManager.updateHousingInfo(condition);
        } catch (Exception e) {
            logger.error("MemberController updateHousingLookTime Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增意见反馈信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addFeedback", method = RequestMethod.POST)
    public String addFeedback(HttpServletRequest request, HttpServletResponse response, HsFeedback feedback) {
        ResultVo result = new ResultVo();
        try {
            Object requestObj = RequestUtil.handleRequestBeanData(feedback);
            HsFeedback feedbackVO = (HsFeedback) requestObj;
            String feedbackContent = StringUtil.trim(feedback.getFeedbackContent());
            String feedbackType = StringUtil.trim(feedback.getFeedbackType() == null ? "" : feedback.getFeedbackType().toString());
            String token = StringUtil.trim(request.getParameter("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (!StringUtil.hasText(feedbackContent) || !StringUtil.hasText(feedbackType)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            feedbackVO.setFeedbackCode(UUID.randomUUID().toString());
            feedbackVO.setCreateBy(Integer.parseInt(memberId));
            //投诉来源 (1外部 2外获 3外看 4区域长 5PC)
            feedbackVO.setPlatform(1);
            result = memberManager.addFeedback(feedbackVO);
        } catch (Exception e) {
            logger.error("MemberController addFeedback Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增会员订阅信息
     * @param request
     * @param response
     * @param hsMemberHousesSubscribe
     * @return
     */
    @RequestMapping(value = "addHousingSubscribe", method = RequestMethod.POST)
    //  @NoRequireLogin
    public String addHousingSubscribe(HttpServletRequest request, HttpServletResponse response, HsMemberHousesSubscribe hsMemberHousesSubscribe) {
        ResultVo result = new ResultVo();
        try {
            Map<Object, Object> condition = new HashMap<>();
            Object requestObj = RequestUtil.handleRequestBeanData(hsMemberHousesSubscribe);
            HsMemberHousesSubscribe memberHousesSubscribe = (HsMemberHousesSubscribe) requestObj;
            String token = StringUtil.trim(request.getParameter("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            //String memberId = hsMemberHousesSubscribe.getMemberId().toString();
            String houseType = memberHousesSubscribe.getHouseType() == null ? "" : memberHousesSubscribe.getHouseType().toString(); //订阅房屋类型（0：出租，1：出售）
            if (!StringUtil.hasText(memberId) || !StringUtil.hasText(houseType)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            String ip = RequestUtil.getIpAddress(request);
            memberHousesSubscribe.setMemberId(Integer.parseInt(memberId));
            condition.put("ip", RequestUtil.getIpAddress(request));
            result = memberManager.addHousingSubscribe(memberHousesSubscribe, condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController addHousingSubscribe Exception message :" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return JsonUtil.toJson(result);
        }

    }

    /**
     * 获取会员订阅信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMemberHousingSubscribe", method = RequestMethod.POST)
    public String getMemberHousingSubscribe(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {

            Map map = RequestUtil.getParameterMap(request);
            String houseType = StringUtil.trim(map.get("houseType")); //订阅房源类型（0：出租，1：出售）
            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (!StringUtil.hasText(houseType) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1"); //当前页
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));
            int isCheck = StringUtil.getAsInt(StringUtil.trim(map.get("isCheck"), "1"));

            condition.put("houseType", houseType);
            condition.put("memberId", memberId);
            condition.put("pageIndex", pageIndex);
            condition.put("isCheck", isCheck);
            condition.put("isDel", 0); //每页显示条数
            condition.put("languageVersion", languageVersion);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
            result = memberManager.getMemberHousingSubscribe(condition);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController getMemberHousingSubscribe Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        } finally {
            return JsonUtil.toJson(result);
        }
    }

    /**
     * 获取会员订阅信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMemberHousingSubscribeMore", method = RequestMethod.POST)
    public String getMemberHousingSubscribeMore(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1");
            int subscribeId = StringUtil.getAsInt(StringUtil.trim(map.get("subscribeId"))); //订阅房源类型（0：出租，1：出售）
            String houseType = StringUtil.trim(map.get("houseType")); //订阅房源类型（0：出租，1：出售）
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));

            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //当前登录会员ID
            condition.putAll(map);
            condition.put("houseType", houseType);
            condition.put("subscribeId", subscribeId);
            condition.remove("languageVersion");
            //condition.put("languageVersion", languageVersion);
            condition.put("pageIndex", pageIndex); //当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //每页显示条数

            result = memberManager.getMemberHousingSubscribeMore(condition);
        } catch (Exception e) {
            logger.error("MemberController getHousesSubscribeList Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 修改会员订阅信息
     * @param request
     * @param response
     * @param hsMemberHousesSubscribe
     * @return
     */
    @RequestMapping(value = "updateMemberHousesSubscribe", method = RequestMethod.POST)
    public String updateMemberHousesSubscribe(HttpServletRequest request, HttpServletResponse response, HsMemberHousesSubscribe hsMemberHousesSubscribe) {
        ResultVo result = new ResultVo();
        try {
            Map<Object, Object> condition = new HashMap<>();
            Object requestObj = RequestUtil.handleRequestBeanData(hsMemberHousesSubscribe);
            HsMemberHousesSubscribe memberHousesSubscribe = (HsMemberHousesSubscribe) requestObj;
            String id = memberHousesSubscribe.getId() == null ? "" : memberHousesSubscribe.getId().toString();
            String token = StringUtil.trim(request.getParameter("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (!StringUtil.hasText(id) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            memberHousesSubscribe.setMemberId(Integer.parseInt(memberId));

            condition.put("ip", RequestUtil.getIpAddress(request));
            result = memberManager.updateMemberRelationInfo(memberHousesSubscribe, condition);

        } catch (Exception e) {
            logger.info("MemberController updateMemberHousesSubscribe Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 删除会员订阅信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "deleteMemberHousesSubscribe", method = RequestMethod.POST)
    public String deleteMemberHousesSubscribe(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String id = StringUtil.trim(map.get("id")); //订阅ID
            if (!StringUtil.hasText(id)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            result = memberManager.deleteMemberRelationInfo(new HsMemberHousesSubscribe(), Integer.parseInt(id));
        } catch (Exception e) {
            logger.error("MemberController deleteMemberHousesSubscribe Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 查询订阅房源列表信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getHousesSubscribeList", method = RequestMethod.POST)
    public String getHousesSubscribeList(HttpServletRequest request, HttpServletResponse response) {

        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1");
            int subscribeId = StringUtil.getAsInt(StringUtil.trim(map.get("subscribeId"))); //订阅房源类型（0：出租，1：出售）
            String houseType = StringUtil.trim(map.get("houseType")); //订阅房源类型（0：出租，1：出售）
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));

            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //当前登录会员ID
            condition.putAll(map);
            //  condition.put("memberId",memberId); //会员ID
            condition.put("houseType", houseType);
            condition.put("languageVersion", languageVersion);
            condition.put("pageIndex", pageIndex); //当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //每页显示条数
            condition.put("isCheck", 1); //每页显示条数
            condition.put("isDel", 0); //每页显示条数
            result = memberManager.getMemberHousing(condition);
        } catch (Exception e) {
            logger.error("MemberController getHousesSubscribeList Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取预约时间表/已看房列表信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getAppointmentTimeOrSeenHouse", method = RequestMethod.POST)
    // @NoRequireLogin
    public String getAppointmentTimeOrSeenHouse(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            int houseType = StringUtil.getAsInt(StringUtil.trim(map.get("houseType"))); //房屋类型 0：出租，1：出售
            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //当前登录会员ID
            //String memberId = StringUtil.trim(map.get("memberId"));
            Integer isFinish = StringUtil.getAsInt(StringUtil.trim(map.get("isFinish"))); //是否完成看房（0：未完成，1：已完成）
            if (houseType < 0 || !StringUtil.hasText(memberId) || isFinish < 0) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.put("houseType", houseType);

            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1");
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));
//            condition.putAll(map);
            condition.put("memberId", memberId);
            condition.put("isCancel", 0); //未取消预约看房
            //未完成看房
            condition.put("isFinish", isFinish);
            //condition.put("isCheck", 1); //审核状态：1：审核通过
//            condition.put("languageVersion", languageVersion);
            condition.put("pageIndex", pageIndex);
            //排除正在预约聊天中的预约信息 开始看房时间不为null
            condition.put("startApartmentTimeNotNull", 0);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
            result = memberManager.getMemberRelationInfo(new HsMemberHousingApplication(), condition);
        } catch (Exception e) {
            logger.error("MemberController getAppointmentTimeOrSeenHouse Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }

        return JsonUtil.toJson(result);
    }

    /**
     * 取消预约看房信息
     * @param request
     * @return
     */
    @RequestMapping(value = "cancelAppointment", method = RequestMethod.POST)
    // @NoRequireLogin
    public String cancelAppointment(HttpServletRequest request) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            String id = request.getParameter("id");
            //token
            String token = StringUtil.trim(request.getParameter("token"));
            //当前登录用户ID
            String memberId = RequestUtil.analysisToken(token).getUserId();
            if (!StringUtil.hasText(id) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.put("id",id);
            condition.put("memberId",memberId);

            result = memberManager.cancelAppointment(condition);
        } catch (Exception e) {
            logger.error("MemberController cancelAppointment Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 投诉业务员
     * @param request
     * @param response
     * @param hsHouseComplain
     * @return
     */
    @RequestMapping(value = "complainSalesman", method = RequestMethod.POST)
    public String complainSalesman(HttpServletRequest request, HttpServletResponse response, HsHouseComplain hsHouseComplain) {
        ResultVo result = new ResultVo();
        try {
            Object requestObj = RequestUtil.handleRequestBeanData(hsHouseComplain);
            HsHouseComplain houseComplain = (HsHouseComplain) requestObj;
            String houseId = houseComplain.getHouseId() == null ? "" : houseComplain.getHouseId().toString(); //房源ID
            String salesmanId = houseComplain.getSalesmanId() == null ? "" : houseComplain.getSalesmanId().toString(); //业务员ID
            String complainReason = houseComplain.getComplainReason() == null ? "" : houseComplain.getComplainReason().toString(); //投诉原因
            String token = StringUtil.trim(request.getParameter("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //当前登录用户ID

            if (!StringUtil.hasText(houseId) || !StringUtil.hasText(salesmanId) || !StringUtil.hasText(complainReason)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            int complainType = houseComplain.getComplainType() == null ? 1 : houseComplain.getComplainType(); //为空，默认为1，投诉业务员
            houseComplain.setComplainType(complainType); //投诉类型
            houseComplain.setCreateBy(Integer.parseInt(memberId));
            result = memberManager.addMemberRelationInfo(houseComplain);

        } catch (Exception e) {
            logger.error("MemberController complainSalesman Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 查询房源预约时间
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getHouseAppointTime", method = RequestMethod.POST)
    @NoRequireLogin
    public String getHouseAppointTime(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();
        try {
            Map map = RequestUtil.getParameterMap(request);
            String startApartmentTime = StringUtil.trim(map.get("startApartmentTime")); //看房日期
            String houseId = StringUtil.trim(map.get("houseId")); //房源ID
            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //用户ID

            if (!StringUtil.hasText(startApartmentTime) || !StringUtil.hasText(houseId) || !StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.putAll(map);
            condition.put("isCancel",0);
            condition.put("isCheck", 1); //审核状态：1审核通过
            result = memberManager.getHouseAppointTime(condition);
        } catch (Exception e) {
            logger.error("MemberController getHouseAppointTime Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增预约看房信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "addAppointmentLookHouse", method = RequestMethod.POST)
    public String addAppointmentLookHouse(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        //查询条件
        Map<Object, Object> condition = new HashMap<>(16);
        try {
            Map map = RequestUtil.getParameterMap(request);
            //房源ID
            String houseId = StringUtil.trim(map.get("houseId"));
            //token
            String token = StringUtil.trim(map.get("token"));
            //当前登录用户ID
            String memberId = RequestUtil.analysisToken(token).getUserId();
            if (!StringUtil.hasText(houseId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            //语言版本
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));
            condition.putAll(map);
            condition.put("languageVersion", languageVersion);
            condition.put("memberId", memberId);

            result = memberManager.addAppointmentLookHouse(condition);
        } catch (Exception e) {
            logger.error("MemberController addAppointmentLookHouse Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 创建预约聊天群
     * @param request
     * @return
     */
    @RequestMapping(value = "createReservationGroup", method = RequestMethod.POST)
    public String createReservationGroup(HttpServletRequest request) {
        ResultVo result = new ResultVo();
        try {
            Map map = RequestUtil.getParameterMap(request);
            //房源id
            String houseId = StringUtil.trim(map.get("houseId"));
            //户主id
            String ownerId = StringUtil.trim(map.get("ownerId"));
            //非空验证
            if (!StringUtil.hasText(houseId) || !StringUtil.hasText(ownerId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            String token = StringUtil.trim(request.getParameter("token"));
            //登陆用户ID
            String memberId = RequestUtil.analysisToken(token).getUserId();
            /**
             * 组成规则
             * 房屋id_户主id_客户id_yy
             */
            StringBuffer groupName = new StringBuffer();
            groupName.append(houseId)
                    .append("_")
                    .append(ownerId)
                    .append("_")
                    .append(memberId)
                    .append("_yy");
            Map<String, Object> group = housesManager.createGroup(groupName.toString(), houseId);
            if(group == null){
                //创建预约会话失败
                result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
                result.setMessage("Create a reservation session failed");
                return JsonUtil.toJson(result);
            }
            result.setDataSet(group);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController createReservationGroup Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 发送预约消息
     * @param request
     * @return
     */
    @RequestMapping(value = "sendReservationMessage", method = RequestMethod.POST)
    public String sendReservationMessage(HttpServletRequest request,@Valid HsMemberHousingApplicationMessage message, BindingResult br) {
        ResultVo result = new ResultVo();
        try {
            //校验数据
            if (br.hasErrors()) {
                FieldError fieldError = br.getFieldErrors().get(0);
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, fieldError.getDefaultMessage()));
            }
            Map map = RequestUtil.getParameterMap(request);
            //操作状态 0 协商 1 同意 2 拒绝 3 预约成功 4 预约失败
            Integer operateStatus = message.getOperateStatus();
            boolean b = operateStatus == 0 || operateStatus == 2;
            if (operateStatus == null || !b) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            Date startApartmentTime = message.getStartApartmentTime();
            Date nowDate = new Date();
            long time = startApartmentTime.getTime();
            long nowtime = nowDate.getTime();
            //预约时间的前两个小时
            time = time - 2 * 60 * 60 * 1000;
            if(time < nowtime){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage("At least 2 hours later");
                return JsonUtil.toJson(result);
            }

            //token
            String token = StringUtil.trim(map.get("token"));
            //当前登录用户ID
            String memberId = RequestUtil.analysisToken(token).getUserId();
            message.setCreateTime(new Date());
            message.setCreateBy(StringUtil.getAsInt(memberId));
            result = memberManager.sendReservationMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController sendReservationMessage Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 同意预约
     *
     * @param request
     * @param message
     * @return
     */
    @RequestMapping(value = "agreeReservation", method = RequestMethod.POST)
    public String agreeReservation(HttpServletRequest request,@Valid HsMemberHousingApplicationMessage message, BindingResult br) {
        ResultVo result = new ResultVo();
        try {
            //校验数据
            if (br.hasErrors()) {
                FieldError fieldError = br.getFieldErrors().get(0);
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, fieldError.getDefaultMessage()));
            }
            Map map = RequestUtil.getParameterMap(request);
            //语言版本
            int languageVersion = StringUtil.getAsInt(request.getParameter("languageVersion"), 1);
            Integer operateStatus = message.getOperateStatus();
            if (operateStatus == null || operateStatus != 1) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            //token
            String token = StringUtil.trim(map.get("token"));
            //当前登录用户ID
            String memberId = RequestUtil.analysisToken(token).getUserId();
            message.setLanguageVersion(languageVersion);
            message.setCreateTime(new Date());
            message.setCreateBy(StringUtil.getAsInt(memberId));

            Map<Object, Object> condition = new HashMap<>(16);
            //房源ID
            String houseId = StringUtil.trim(map.get("houseId"));
            //房源类型
            String houseType = StringUtil.trim(map.get("houseType"));
            //钥匙是否在平台：0>无，1>业务员或钥匙保管员处 , 2>房间未上锁，直接看，3>在物业，保安处
            String haveKey = StringUtil.trim(map.get("haveKey"));
            //预约看房时间
            String startApartmentTime = StringUtil.trim(map.get("startApartmentTime"));
            //环信群id
            String groupId = StringUtil.trim(map.get("groupId"));
            //环信群名称
            String groupName = StringUtil.trim(map.get("groupName"));

            condition.put("languageVersion", languageVersion);
            condition.put("groupId", groupId);
            condition.put("groupName", groupName);
            condition.put("startApartmentTime", startApartmentTime);
            condition.put("memberId", memberId);
            condition.put("houseId", houseId);
            condition.put("appointmentType", 0);
            condition.put("haveKey", haveKey);
            condition.put("houseType", houseType);
            //用于标识是否与业主沟通成功
            condition.put("isReservation", true);
            result = memberManager.agreeReservation(message, condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController agreeReservation Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取预约聊天记录
     * @param request
     * @return
     */
    @RequestMapping(value = "getReservationHistory", method = RequestMethod.POST)
    public String getReservationHistory(HttpServletRequest request) {
        ResultVo result = new ResultVo();
        try {
            Map map = RequestUtil.getParameterMap(request);
            //房源id
            String groupId = StringUtil.trim(map.get("groupId"));
            //非空验证
            if (!StringUtil.hasText(groupId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            Map<Object, Object> condition = new HashMap<>(1);
            condition.put("groupId", groupId);
            condition.put("isDel", 0);
            result = memberManager.getReservationHistory(condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController createReservationGroup Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));

    }

    /**
     * 发送议价消息
     *
     * @param request
     * @param message
     * @param br
     * @return
     */
    @RequestMapping(value = "/bargain/message/send", method = RequestMethod.POST)
    public String sendBargainMessage(HttpServletRequest request, @Valid HsMemberHousingBargainMessage message, BindingResult br) {
        ResultVo result = null;
        try {
            //校验数据
            if (br.hasErrors()) {
                FieldError fieldError = br.getFieldErrors().get(0);
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, fieldError.getDefaultMessage()));
            }
            message.setOperateStatus(0);
            //语言版本
            int languageVersion = StringUtil.getAsInt(request.getParameter("languageVersion"), 1);
            message.setLanguageVersion(languageVersion);
            message.setCreateTime(new Date());
            result = memberManager.sendBargainMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController sendBargainMessage Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 同意议价
     * @param request
     * @param message
     * @param br
     * @return
     */
    @RequestMapping(value = "/bargain/agreeBargain", method = RequestMethod.POST)
    public String agreeBargain(HttpServletRequest request, @Valid HsMemberHousingBargainMessage message, BindingResult br) {
        ResultVo result = null;
        try {
            if (br.hasErrors()) {//校验数据
                FieldError fieldError = br.getFieldErrors().get(0);
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, fieldError.getDefaultMessage()));
            }
            message.setOperateStatus(1);
            int languageVersion = StringUtil.getAsInt(request.getParameter("languageVersion"), 1); //语言版本
            message.setLanguageVersion(languageVersion);
            message.setCreateTime(new Date());
            result = memberManager.agreeBargain(message);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController sendBargainMessage Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 拒绝议价
     * @param request
     * @param message
     * @param br
     * @return
     */
    @RequestMapping(value = "/bargain/refuseBargain", method = RequestMethod.POST)
    public String refuseBargain(HttpServletRequest request, @Valid HsMemberHousingBargainMessage message, BindingResult br) {
        ResultVo result = null;
        try {
            if (br.hasErrors()) {
                //校验数据
                FieldError fieldError = br.getFieldErrors().get(0);
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR, fieldError.getDefaultMessage()));
            }
            message.setOperateStatus(2);
            //语言版本
            int languageVersion = StringUtil.getAsInt(request.getParameter("languageVersion"), 1);
            message.setLanguageVersion(languageVersion);
            message.setCreateTime(new Date());
            result = memberManager.refuseBargain(message);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController sendBargainMessage Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 议价记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/bargain/record", method = RequestMethod.POST)
    public String getBargainRecord(HttpServletRequest request) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = Maps.newHashMap();
        try {
            Map map = RequestUtil.getParameterMap(request);
            //议价群id
            String bargainId = StringUtil.trim(map.get("bargainId"));
            if (!StringUtil.hasText(bargainId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }
            condition.put("bargainId", bargainId);
            condition.put("isDel", 0);
            result = memberManager.getBargainRecord(condition);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController getBargainRecord Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 我的议价列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/get/bargains/list", method = RequestMethod.POST)
    public String getBargainsList(HttpServletRequest request) {
        ResultVo result = new ResultVo();
        try {
            String token = request.getParameter("token"); //登录token
            //查询类型 0出租 1出售  不传查询所有
            int houseType = StringUtil.getAsInt(request.getParameter("houseType"), -1);
            String memberId = RequestUtil.analysisToken(token).getUserId();
            if (!StringUtil.hasText(memberId)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            Map<Object, Object> condition = Maps.newHashMap();
            condition.put("memberId", memberId);
            if (houseType != -1) {

                condition.put("houseType", houseType);
            }
            result = memberManager.getBargainsList(condition);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MemberController getBargainsList Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 查询房源进度
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getMemberHousingProgress", method = RequestMethod.POST)
    // @NoRequireLogin
    public String getMemberHousingProgress(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<Object, Object>();

        try {
            Map map = RequestUtil.getParameterMap(request);
            String token = StringUtil.trim(map.get("token")); //token
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            String leaseType = StringUtil.trim(map.get("leaseType")); //房屋类型 0：出租，1：出售

            if (!StringUtil.hasText(memberId) || !StringUtil.hasText(leaseType)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1");
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));
            condition.put("memberId", memberId);
            condition.put("leaseType", leaseType);
            condition.put("languageVersion", languageVersion);
            condition.put("pageIndex", pageIndex); //当前页
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE); //每页显示条数
            result = memberManager.getMemberHousingProgress(condition);

        } catch (Exception e) {
            logger.error("MemberController getMemberHousingProgress Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
        }

        return JsonUtil.toJson(result);
    }


    /**
     * 获取业主首页数据
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getOwnerIndexData", method = RequestMethod.POST)
    @NoRequireLogin
    public String getOwnerIndexData(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        try {
            Map<Object, Object> map = RequestUtil.getParameterMap(request);
            int language = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion")), 0); //语言版本
            Map<Object, Object> condition = new HashMap<>();
            condition.put("channelAliasName", "hs_app_owner_index"); //业主首页
            condition.put("language", language);
            condition.put("channelType", 3); //频道类型（1：PC,2:触屏，3：APP）
            condition.put("channelState", 1); //频道状态（0：不启用，1：启用）

            result = memberManager.getOwnerIndexData(condition);
        } catch (Exception e) {
            logger.error("MemberController getOwnerIndexData Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 创建订单
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/order/create", method = RequestMethod.POST)
    public String createOrder(HttpServletRequest request) {
        ResultVo vo = null;
        try {
            Map map = RequestUtil.getParameterMap(request);
            int bargainId = StringUtil.getAsInt(StringUtil.trim(map.get("bargainId")), -1);//议价ID
            int houseId = StringUtil.getAsInt(StringUtil.trim(map.get("houseId")), -1);//房源ID
            String token = StringUtil.trim(request.getParameter("token"));
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(request.getParameter("languageVersion")), 0);
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (houseId == -1 || bargainId == -1) {
                vo = ResultVo.error(
                        ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,
                        ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE
                );
                return JsonUtil.toJson(vo);
            }
            Map<String, Object> condition = new HashMap<>();
            condition.put("houseId", houseId);
            condition.put("memberId", StringUtil.getAsInt(memberId));
            condition.put("bargainId", 1);
            condition.put("languageVersion", languageVersion);
            vo = housesManager.createOrder(condition);
        } catch (Exception e) {
            logger.error("HousesController controller: createOrder Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }


    /**
     * 测试订单支付
     * @param request
     * @return
     */
    @RequestMapping(value = "/order/pay", method = RequestMethod.POST)
    public String testOrderPay(HttpServletRequest request) {
        ResultVo vo = null;
        try {
            Map map = RequestUtil.getParameterMap(request);
            String orderCode = StringUtil.trim(map.get("orderCode"));//订单编码
            String token = StringUtil.trim(request.getParameter("token"));
            String memberId = RequestUtil.analysisToken(token).getUserId(); //会员ID
            if (!StringUtil.hasText(orderCode) || !StringUtil.hasText(memberId)) {
                vo = ResultVo.error(
                        ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,
                        ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE
                );
                return JsonUtil.toJson(vo);
            }
            Map<Object, Object> condition = new HashMap<>();
            condition.put("orderCode", orderCode);
            condition.put("memberId", memberId);
            vo = payManager.testOrderPay(condition);
        } catch (Exception e) {
            logger.error("HousesController controller: orderPay Exception message:" + e.getMessage());
            vo = ResultVo.error(
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR,
                    ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE
            );
        }
        return JsonUtil.toJson(vo);
    }


    /**
     * 获取变价房源信息
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getChangePriceHouse", method = RequestMethod.POST)
    public String getChangePriceHouse(HttpServletRequest request, HttpServletResponse response) {
        ResultVo result = new ResultVo();
        Map<Object, Object> condition = new HashMap<>();

        try {
            Map map = RequestUtil.getParameterMap(request);
            String houseType = StringUtil.trim(map.get("houseType")); //0：出租，1：出售
            String token = StringUtil.trim(map.get("token"));
            String memberId = RequestUtil.analysisToken(token).getUserId();

            if (!StringUtil.hasText(houseType)) {
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
                return JsonUtil.toJson(result);
            }

            condition.putAll(map);
            String pageIndex = StringUtil.trim(map.get("pageIndex"), "1");
            int languageVersion = StringUtil.getAsInt(StringUtil.trim(map.get("languageVersion"), "0"));
            condition.put("houseType", houseType);
            condition.put("memberId", memberId);
            condition.put("languageVersion", languageVersion);
            condition.put("pageIndex", pageIndex);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);

            result = memberManager.getChangePriceHouse(condition); //获取变价信息
        } catch (Exception e) {
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 推荐房源
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "recommendHouses", method = RequestMethod.POST)
    @NoRequireLogin
    public String recommendHouses(@RequestParam(required = false) String token) {
        ResultVo result = new ResultVo();
        try {
            Map<String, Object> map = commonManager.recommendHouses(token);
            result.setDataSet(map);
        } catch (Exception e) {
            logger.error("MemberController recommendHouses Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);

    }

    /**
     * 客户议价成功后，新增客户购房信息
     * @param request
     * @param purchase
     * @return
     */
    @RequestMapping(value = "addPurchaseInfo", method = RequestMethod.POST)
    public String addPurchaseInfo(HttpServletRequest request, HsMemberPurchase purchase) {
        ResultVo result = new ResultVo();
        try {
            //登录token
            String token = request.getParameter("token");
            String memberId = RequestUtil.analysisToken(token).getUserId();
            purchase.setMemberId(StringUtil.getAsInt(memberId));
            purchase.setCreateBy(StringUtil.getAsInt(memberId));
            purchase.setCreateTime(new Date());
            result = memberManager.addPurchaseInfo(purchase);
        } catch (Exception e) {
            logger.error("MemberController sendBargainMessage Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 人员议价成功后，获取人员购房信息
     * @param request
     * @return
     */
    @RequestMapping(value = "getPurchaseInfo", method = RequestMethod.POST)
    public String getPurchaseInfo(HttpServletRequest request) {
        ResultVo result = new ResultVo();
        try {
            //登录token
            String token = request.getParameter("token");
            //订单id
            String orderId = request.getParameter("orderId");
            String memberId = RequestUtil.analysisToken(token).getUserId();
            Map<Object, Object> condition = new HashMap<>(2);
            condition.put("memberId", memberId);
            condition.put("orderId", orderId);
            result = memberManager.getPurchaseInfo(condition);
        } catch (Exception e) {
            logger.error("MemberController getPurchaseInfo Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR);
            result.setMessage(ResultConstant.SYS_REQUIRED_TIMEOUT_ERROR_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 新增人员贷款信息
     * @param request
     * @param financialLoansApply
     * @return
     */
    @RequestMapping(value="/addLoansApply", method = RequestMethod.POST)
    public String addLoansApply(HttpServletRequest request ,HsMemberFinancialLoansApply financialLoansApply){
        ResultVo result = new ResultVo();
        try{
            //登录token
            String token = request.getParameter("token");
            //订单id
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            /*dateBirth1字段接受不到参数，用字符串接收，然后再赋值*/
            String dateBirth1 = request.getParameter("dateBirth1");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateBirth = sdf.parse(dateBirth1);
            financialLoansApply.setDateBirth(dateBirth);
            financialLoansApply.setMemberId(memberId);
            financialLoansApply.setCreateTime(new Date());
            financialLoansApply.setCreateBy(memberId);
            result = memberManager.addLoansApply(financialLoansApply);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MemberController getOwnerHousingApplication Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取人员贷款信息
     * @param request
     * @return
     */
    @RequestMapping(value="/getLoansApply", method = RequestMethod.POST)
    public String getLoansApply(HttpServletRequest request){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>(1);
        try{
            //登录token
            String token = request.getParameter("token");
            //订单id
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            condition.put("isDel",0);
            condition.put("memberId",memberId);
            result = memberManager.getLoansApply(condition);
        }catch (Exception e){
            logger.error("MemberController getLoansApply Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     *  获取人员申购预约列表
     * @param request
     * @return
     */
    @RequestMapping(value="/getPurchaseApplyList", method = RequestMethod.POST)
    public String getPurchaseApplyList(HttpServletRequest request){
        ResultVo result = new ResultVo();
        Map<Object,Object> condition = new HashMap<>(1);
        try{
            //当前页
            int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"), 1);
            //登录token
            String token = request.getParameter("token");
            //订单id
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            condition.put("pageIndex", pageIndex);
            //页显示条数
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
            condition.put("isDel",0);
            condition.put("memberId",memberId);
            result = memberManager.getPurchaseApplyList(condition);
        }catch (Exception e){
            logger.error("MemberController getPurchaseApplyList Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 获取人员申购预约详情
     * @param request
     * @return
     */
    @RequestMapping(value="/getPurchaseApplyDetails", method = RequestMethod.POST)
    public String getPurchaseApplyDetails(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            //当前页
            int id = StringUtil.getAsInt(request.getParameter("id"));
            result = memberManager.getPurchaseApplyDetails(id);
        }catch (Exception e){
            logger.error("MemberController getPurchaseApplyDetails Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }



    /**
     * 保存开发商直售楼盘个人信息填写
     * @param request
     * @return
     */
    @RequestMapping(value="/saveNewBuildingMemberApply", method = RequestMethod.POST)
    public String saveNewBuildingMemberApply(HttpServletRequest request,HsHouseNewBuildingMemberApply memberApply){
        ResultVo result = new ResultVo();
        try{
            //登录token
            String token = request.getParameter("token");
            //订单id
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            memberApply.setMemberId(memberId);
            if(null == memberApply.getProjectId()){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            }
            result = memberManager.saveNewBuildingMemberApply(memberApply);
        }catch (Exception e){
            logger.error("MemberController saveNewBuildingMemberApply Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 修改个人楼盘申购信息
     * @param request
     * @return
     */
    @RequestMapping(value="/updateNewBuildingMemberApply", method = RequestMethod.POST)
    public String updateNewBuildingMemberApply(HttpServletRequest request,HsHouseNewBuildingMemberApply memberApply){
        ResultVo result = new ResultVo();
        try{
            //登录token
            String token = request.getParameter("token");
            //订单id
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            memberApply.setUpdateBy(memberId);
            memberApply.setUpdateTime(new Date());
            if(null == memberApply.getId()){
                result.setResult(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR);
                result.setMessage(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE);
            }
            result = memberManager.updateNewBuildingMemberApply(memberApply);
        }catch (Exception e){
            logger.error("MemberController updateNewBuildingMemberApply Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 下载申购合同
     * @param request
     * @return
     */
    @RequestMapping(value="/download/purchase/pdf")
    public ResponseEntity<byte[]> downloadPurchase(HttpServletRequest request, HttpServletResponse response){
        try{
            //登录token
            String token = request.getParameter("token");
            //项目id
            int projectId = StringUtil.getAsInt(StringUtil.trim(request.getParameter("projectId")));
            //人员id
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            if(projectId < 1){
                return null;
            }
            Map<Object,Object> condition = Maps.newHashMap();
            condition.put("isDel",0);
            condition.put("memberId",memberId);
            condition.put("projectId",projectId);
            byte[] bytes = memberManager.downloadPurchase(condition);

            HttpHeaders headers = new HttpHeaders();
            String fileName = null;
            try {
                //解决中文乱码
                fileName = new String(("SubscriptionContract.pdf").getBytes("gb2312"),"iso-8859-1");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            headers.setContentDispositionFormData("attachment", fileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取开发商直售楼盘个人信息
     * @param request
     * @return
     */
    @RequestMapping(value="/getNewBuildingMemberApply", method = RequestMethod.POST)
    public String getNewBuildingMemberApply(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            //登录token
            String token = request.getParameter("token");
            //项目id
            int projectId = StringUtil.getAsInt(StringUtil.trim(request.getParameter("projectId")));
            //人员id
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            if(projectId < 1){
                return JsonUtil.toJson(ResultVo.error(ResultConstant.SYS_REQUIRED_PARAMETER_ERROR,ResultConstant.SYS_REQUIRED_PARAMETER_ERROR_VALUE));
            }
            Map<Object,Object> condition = Maps.newHashMap();
            condition.put("isDel",0);
            condition.put("memberId",memberId);
            condition.put("projectId",projectId);
            result = memberManager.getNewBuildingMemberApply(condition);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("MemberController getNewBuildingMemberApply Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }


    /**
     * 获取交易订单列表
     * @param request
     * @return
     */
    @RequestMapping(value="/myOrder/list", method = RequestMethod.POST)
    public String getMemberOrderList(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            Map<Object,Object> condition = Maps.newHashMap();
            int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"), 1); //当前页
            int orderType = StringUtil.getAsInt(request.getParameter("orderType"),-1);//订单类型 0-租房->1-买房
            String token = request.getParameter("token"); //登录token
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            if(orderType!=-1){
                condition.put("orderType",orderType);
            }
            condition.put("memberId",memberId);
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
            condition.put("isDel", 0);
            condition.put("isCancel", 0);
            result = memberManager.getMemberOrderList(condition);
        }catch (Exception e){
            logger.error("MemberController getPurchaseApplyDetails Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }

    /**
     * 我的消费记录列表
     * @param request
     * @return
     */
    @RequestMapping(value="/expense/calendars", method = RequestMethod.POST)
    public String getExpenseCalendarsList(HttpServletRequest request){
        ResultVo result = new ResultVo();
        try{
            Map<Object,Object> condition = Maps.newHashMap();
            int pageIndex = StringUtil.getAsInt(request.getParameter("pageIndex"), 1); //当前页
            String token = request.getParameter("token"); //登录token
            int memberId = StringUtil.getAsInt(RequestUtil.analysisToken(token).getUserId());
            condition.put("queryMonthsAgo",3);//查询前几个月的数据,
            condition.put("memberId",memberId);
            condition.put("pageIndex",pageIndex);
            condition.put("pageSize", AppRquestParamsConstant.APP_PAGE_SIZE);
//            condition.put("payStatus", 0);//支付状态 0-未付款 1- 已支付
//            condition.put("orderStatus", 9);//订单状态 9已完成
            condition.put("isDel", 0);
            condition.put("isCancel", 0);
            result = memberManager.getExpenseCalendarsList(condition);
        }catch (Exception e){
            logger.error("MemberController getExpenseCalendarsList Exception message:" + e.getMessage());
            result.setResult(ResultConstant.SYS_REQUIRED_FAILURE);
            result.setMessage(ResultConstant.SYS_REQUIRED_FAILURE_VALUE);
        }
        return JsonUtil.toJson(result);
    }
}